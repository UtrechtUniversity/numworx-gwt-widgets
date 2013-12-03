package fi.grafiek3dgwt.client;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import fi.grafiek3dgwt.client.formuleobjects.*;
import fi.grafiek3dgwt.client.expressies.*;
//import fi.grafiek3dtest.tekstobjects.*;

public class Grafiek3DComponent //extends JPanel implements ActionListener
{

	public static final CssColor darkGreen = CssColor.make(41, 156, 57);
    public static final CssColor mediumGreen = CssColor.make(173, 222, 99);
    public static final CssColor brownRed = CssColor.make(214, 0, 0);
    public static final CssColor lightRed = CssColor.make(255, 156, 74);
    public static final CssColor mediumBlue = CssColor.make(99, 198, 222);
    public static final CssColor lightGray = CssColor.make(192,192,192); 
    public static final CssColor black = CssColor.make(0,0,0);
    public static final CssColor red = CssColor.make(255,0,0);
    public static final CssColor green = CssColor.make(0,255,0);
    public static final CssColor blue = CssColor.make(0,0,255);
    public static final CssColor yellow = CssColor.make(255,255,0);
    public static final CssColor cyan = CssColor.make(0,255,255);
    public static final CssColor magenta = CssColor.make(255,0,255);
    public static final CssColor white = CssColor.make(255,255,255);
    public static final CssColor gray = CssColor.make(120,120,120);
	
    public static final CssColor transYellow = yellow; 
    	//new Color(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 200);
    public static final CssColor transCyan = cyan; 
    	//new Color(Color.cyan.getRed(), Color.cyan.getGreen(), Color.cyan.getBlue(), 200);
    public static final CssColor transMagenta = magenta; 
    	//new Color(Color.magenta.getRed(), Color.magenta.getGreen(), Color.magenta.getBlue(), 200);
    public static final CssColor transGreen = green; 
    	//new Color(Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue(), 200);
    
    // drawing colors
    public static CssColor axesColor = black;
    // floorColor is a dummy
    public static CssColor floorColor = white;
    public static CssColor floorOutlineColor = black;
    
    public static CssColor objectColor = transYellow;
    
    public static CssColor graphColor = transYellow;
    public static CssColor surfaceColor = transYellow;
    public static CssColor graphOutlineColor = lightGray;
    public static CssColor surfaceOutlineColor = lightGray;
    public static CssColor curveColor = brownRed;    
    public static CssColor curveOutlineColor = brownRed;
    public static CssColor wireFrameColor = brownRed;
    

	//public static Font assenFont = new Font("SansSerif",Font.PLAIN, 10);
	
    public static double MAXZOOM = 15e-1d;
    public static double MINZOOM = 2e-1d; 
    public static double ZOOMSTEP = 1e-1d;
    public static double defaultZoom = 7e-1d;
    public double zoom = defaultZoom;
    
    // projections
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    public int defaultProjection = CENTRALPROJ;
    //public int defaultProjection = PARALLELPROJ;
    
    // mouse modes
    public static final int INERT = 0;
    // default for mouseMode
    public int mouseMode = INERT;
    public int oldMouseMode;
    
    // listener for mouse movements on panel3D
    //MLMML listener;

    // managing the cursor
    // coordinates
//    int xClicked;
//    int yClicked;
//    int xMoved;
//    int yMoved;

    // circle radius for rotate modes
    public static double RADFACTOR = 8e-1d;//1d;
    
//    JScrollPane scrollPane;
//    ScrollPanel scrollPanel;
//    int scrollPanelWidth = 450;
//    int scrollPaneWidth = 470;
//    int scrollBarWidth = 20;
    
    // hoeken
    double angleXG = Object3DContainer.angleXStart;
    double angleZG = Object3DContainer.angleZStart;
    double angleXS = Object3DContainer.angleXStart;
    double angleZS = Object3DContainer.angleZStart;
    double angleXC = Object3DContainer.angleXStart;
    double angleZC = Object3DContainer.angleZStart;
    
    // axes
    double xMinBegin = -2, xMaxBegin = 2, xStepBegin = 5e-1d, 
    	   yMinBegin = -2, yMaxBegin = 2, yStepBegin = 5e-1d, 
    	   zMinBegin = -2, zMaxBegin = 2, zStepBegin = 5e-1d;
    double xMinG = -2, xMaxG = 2, xStepG = 5e-1d, 
           yMinG = -2, yMaxG = 2, yStepG = 5e-1d, 
           zMinG = -2, zMaxG = 2, zStepG = 5e-1d;
    int xFinerStepsBegin = 2;
    int yFinerStepsBegin = 2;
	int xFinerStepsG = 2;
	int yFinerStepsG = 2;

    double xMinS = -2, xMaxS = 2, xStepS = 5e-1d, 
    	   yMinS = -2, yMaxS = 2, yStepS = 5e-1d, 
    	   zMinS = -2, zMaxS = 2, zStepS = 5e-1d;
	int xFinerStepsS = 2;
	int yFinerStepsS = 2;
    
    double xMinC = -2, xMaxC = 2, xStepC = 5e-1d, 
	   	   yMinC = -2, yMaxC = 2, yStepC = 5e-1d, 
	   	   zMinC = -2, zMaxC = 2, zStepC = 5e-1d;
	int xFinerStepsC = 2;
	int yFinerStepsC = 2;
    
	
	// state
    int zoomFactorG = 0;
    int translateXFactorG = 0;
    int translateYFactorG = 0;
    int translateZFactorG = 0;
    int finerFactorG = 0;

    int zoomFactorS = 0;
    int translateXFactorS = 0;
    int translateYFactorS = 0;
    int translateZFactorS = 0;
    //int finerFactorS = 0;
    
    int zoomFactorC = 0;
    int translateXFactorC = 0;
    int translateYFactorC = 0;
    int translateZFactorC = 0;
    //int finerFactorC = 0;
    
    
    // state    
    public boolean noAxesG = false;
    public boolean noAxesS = false;
    public boolean noAxesC = false;
    
    public static final int NOFLOOR = 0;
    public static final int TRANSFLOOR = 1;
//    public static final int SOLIDFLOOR = 2;

    // state
    int floorTypeG = NOFLOOR;
    int floorTypeS = NOFLOOR;
    int floorTypeC = NOFLOOR;
    
    public static final int NOLABELS = 0;
    public static final int ENDLABELS = 1;
    public static final int ALLLABELS = 2;
    // state
    int labelTypeG = ENDLABELS;
    int labelTypeS = ENDLABELS;
    int labelTypeC = ENDLABELS;
    
    // state
    boolean wireFrameG = false;
    boolean wireFrameS = false;
    
    boolean centraleProjG = true;
    boolean centraleProjS = true;
    boolean centraleProjC = true;
    
    //Color colorG = transYellow;
    //Color colorS = transYellow;
    
    long lastActionTime = 0;
    
    
    // the 3D panel(s)
    Object3DContainer panel3D;// = new Object3DContainer();
    //int panel3DSize = 400;
    
    // the function editor
    //FunctieEditor functieEditor;
    //int functieEditorWidth = 350;
    //int functieEditorHeight = 500;
    
    
//    JPanel knoppenPanel;
//    int knoppenPanelWidth = 40; 
 
    ObjectGroup3D currentObjectGroup;
    
    public static final int FUNCTION = 0;
    public static final int SURFACE = 1;
    public static final int CURVE = 2;
    // state
    int objectType = FUNCTION;
    
    //FormuleButton zoomStandaardButton, zoomInButton, zoomUitButton, transPlusButton, asNaamButton, transMinButton, 
    // 			  solidDraadKeuzeButton, finerPlusButton, finerMinButton, asKeuzeButton, labelKeuzeButton,
    //			  projectieKeuzeButton, kleurKeuzeButton;
    
    //JPopupMenu assenPopup, labelsPopup, kleurenPopup;
    
	String varNaamX = "x";
	String varNaamY = "y";
	String paramNaam = "t";
	String paramNaamU = "u";
	String paramNaamV = "v";

	boolean checkForAsymptotes = false;
	
	Axes axesObject;
	// state
	Expressie grafiek3DExpressie = null;
	Grafiek3D grafiek3DObject;
	// state
	Expressie surfaceXExpressie;
	Expressie surfaceYExpressie;
	Expressie surfaceZExpressie;
	double uMin = 0;
	double uMax = 2;
	int uPoints = 10;
	double vMin = 0;
	double vMax = 2;
	int vPoints = 10;
	Surface3D surface3DObject;	

	// state
	Expressie curveXExpressie;
	Expressie curveYExpressie;
	Expressie curveZExpressie;
	double tMin = 0;
	double tMax = 2;
	int tPoints = 10;
	Curve3D curve3DObject;

	Canvas grafiek3DCanvas;
	Context2d grafiek3DContext2d;
	int breedte, hoogte;
	Grafiek3DGWT owner;
	
	boolean dragging;
	int xStart, yStart, xClicked, yClicked;
	
	// edit state variables
	boolean zoomOptie = true;
	boolean translateOptie = true;
	boolean solidDraadKeuzeOptie = true;
	boolean finerKeuzeOptie = true;
	boolean asKeuzeOptie = true;
	boolean labelKeuzeOptie = true;
	boolean projectieKeuzeOptie = true;
	boolean kleurKeuzeOptie = true;
	boolean figuurIsDemo = false;
	
	
    //public Grafiek3DComponent(int x, int y, int w, int h, Hashtable ims, String[] imNames)
	public Grafiek3DComponent(Grafiek3DGWT o, int b, int h)
    {
		owner = o;
		  
	  	breedte = b;
	  	hoogte = h;
	  
	  	grafiek3DCanvas = Canvas.createIfSupported();
	  	grafiek3DCanvas.setWidth(breedte + "px");
	  	grafiek3DCanvas.setHeight(hoogte + "px");
	  	grafiek3DCanvas.setCoordinateSpaceWidth(breedte);
	  	grafiek3DCanvas.setCoordinateSpaceHeight(hoogte);

	  	grafiek3DCanvas.addStyleName(owner.grafiek3DGWTCss.canvas());    	
	  	
	  	
	  	MouseHandler mouseHandler = new MouseHandler();
	  	grafiek3DCanvas.addMouseDownHandler(mouseHandler);
	  	grafiek3DCanvas.addMouseMoveHandler(mouseHandler);
	  	grafiek3DCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	grafiek3DCanvas.addTouchStartHandler(touchHandler);
	  	grafiek3DCanvas.addTouchMoveHandler(touchHandler);
	  	grafiek3DCanvas.addTouchEndHandler(touchHandler);
	  
	  	grafiek3DContext2d = grafiek3DCanvas.getContext2d();
	  	
	  	// dit geeft wel heel wonderlijke effecten !!
	  	//grafiek3DContext2d.setGlobalAlpha(0.8d);
	  	
	  	
	  	panel3D = new Object3DContainer(grafiek3DContext2d, breedte, hoogte);

    	//scrollPanel = new JPanel();
//    	scrollPanel = new ScrollPanel();
//    	scrollPanel.setLayout(null);
//    	scrollPanel.setSize(w - scrollBarWidth, 10 + panel3DSize + functieEditorHeight);
//    	scrollPanel.setPreferredSize(new Dimension(w - scrollBarWidth, 10 + panel3DSize + functieEditorHeight));    	
    	
    	//panel3D.setBounds(10, 10, panel3DSize, panel3DSize);
    	//scrollPanel.add(panel3D);
    	
    	//functieEditor = new FunctieEditor(false);
    	//functieEditor.setBounds(10,
    	//						panel3D.getLocation().y + panel3D.getSize().height, 
    	//						panel3DSize, functieEditorHeight);
    	//functieEditor.zetGrafiek3DComponent(this);
    	//functieEditor.zetFuncties(objectType, false);
    	//scrollPanel.add(functieEditor);                        
    	
    	//scrollPane = new JScrollPane(scrollPanel);
    	//scrollPane.setBounds(0, 0, w, h);
    	//scrollPane.setPreferredSize(new Dimension(w, h));
    	//add(scrollPane);
    	
    	//knoppenPanel = new JPanel();
    	//knoppenPanel.setLayout(null);
    	//knoppenPanel.setOpaque(false);
    	//knoppenPanel.setBounds(10 + panel3DSize, 0, w - panel3DSize - 30, panel3DSize);
    	
    	
    	//scrollPanel.add(knoppenPanel);
//System.out.println("kpw = " + knoppenPanel.getWidth());    	

/*    	
    	zoomStandaardButton	= new ZoomKnop("standaard");
    	zoomStandaardButton.setVisible(false);
    	knoppenPanel.add(zoomStandaardButton);
    	zoomStandaardButton.addActionListener(this);
    	
		zoomInButton = new ZoomKnop("zoomin", getImage("zoominknop.gif"));
    	zoomInButton.setVisible(false);
    	knoppenPanel.add(zoomInButton);
    	zoomInButton.addActionListener(this);
		
		zoomUitButton = new ZoomKnop("zoomuit", getImage("zoomuitknop.gif"));
    	zoomUitButton.setVisible(false);
    	knoppenPanel.add(zoomUitButton);
    	zoomUitButton.addActionListener(this);
		
		transPlusButton = new ZoomKnop("transplus");
    	transPlusButton.setVisible(false);
    	knoppenPanel.add(transPlusButton);
    	transPlusButton.addActionListener(this);    	
		
		asNaamButton = new ZoomKnop("xasnaam");
    	asNaamButton.setVisible(false);
    	knoppenPanel.add(asNaamButton);
    	asNaamButton.addActionListener(this);        	
		
		transMinButton = new ZoomKnop("transmin");
    	transMinButton.setVisible(false);
		knoppenPanel.add(transMinButton);
    	transMinButton.addActionListener(this);    		
		
		solidDraadKeuzeButton = new ZoomKnop("draad");
		solidDraadKeuzeButton.setVisible(false);
      	knoppenPanel.add(solidDraadKeuzeButton);
    	solidDraadKeuzeButton.addActionListener(this);
		
		finerPlusButton = new ZoomKnop("finerplus");
    	finerPlusButton.setVisible(false);
    	knoppenPanel.add(finerPlusButton);
    	finerPlusButton.addActionListener(this);
		
		finerMinButton = new ZoomKnop("finermin");
    	finerMinButton.setVisible(false);
    	knoppenPanel.add(finerMinButton);
    	finerMinButton.addActionListener(this);
		
		asKeuzeButton = new ZoomKnop("askeuze");
    	asKeuzeButton.setVisible(false);
		knoppenPanel.add(asKeuzeButton);
       	asKeuzeButton.addActionListener(this);

		labelKeuzeButton = new ZoomKnop("labelkeuze");
    	labelKeuzeButton.setVisible(false);
		knoppenPanel.add(labelKeuzeButton);
    	labelKeuzeButton.addActionListener(this);
		
		projectieKeuzeButton = new ZoomKnop("central");
		projectieKeuzeButton.setVisible(false);
		knoppenPanel.add(projectieKeuzeButton);
		projectieKeuzeButton.addActionListener(this);

		kleurKeuzeButton = new ZoomKnop("kleurkeuze");
		kleurKeuzeButton.setVisible(false);
		knoppenPanel.add(kleurKeuzeButton);
		kleurKeuzeButton.addActionListener(this);
*/		
    	//layoutKnoppenPanel();    	
    	
/*    	
		assenPopup = new JPopupMenu();
		JMenuItem mi = new JMenuItem(Grafiek3DTest.rb.getString("geenAssenTekst"));
		mi.addActionListener(this);
		assenPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("xyzAsTekst"));
		mi.addActionListener(this);
		assenPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("xyVloerTekst"));
		mi.addActionListener(this);
		assenPopup.add(mi);
		
		add(assenPopup);
*/
/*    	
		labelsPopup = new JPopupMenu();
		mi = new JMenuItem(Grafiek3DTest.rb.getString("geenLabelsTekst"));
		mi.addActionListener(this);
		labelsPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("eindLabelsTekst"));
		mi.addActionListener(this);
		labelsPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("alleLabelsTekst"));
		mi.addActionListener(this);
		labelsPopup.add(mi);
		
		add(labelsPopup);
*/
/*    	
		kleurenPopup = new JPopupMenu();
		mi = new JMenuItem(Grafiek3DTest.rb.getString("geelTekst"));
		mi.setBackground(transYellow);
		mi.addActionListener(this);
		kleurenPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("cyanTekst"));
		mi.setBackground(transCyan);
		mi.addActionListener(this);
		kleurenPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("magentaTekst"));
		mi.setBackground(transMagenta);
		mi.addActionListener(this);
		kleurenPopup.add(mi);
		mi = new JMenuItem(Grafiek3DTest.rb.getString("groenTekst"));
		mi.setBackground(transGreen);
		mi.addActionListener(this);
		kleurenPopup.add(mi);
		
		add(kleurenPopup);
*/		
        
//        setNewModel(0, true);
    	
    }
    
    public void setSize(int b, int h)
    {
    	
    	// breedtes
    	//scrollPane.setSize(b, h);
    	//scrollPane.setPreferredSize(new Dimension(b, h));
    	//scrollPanel.setSize(b - scrollBarWidth, 10 + panel3DSize + functieEditorHeight);
    	//scrollPanel.setPreferredSize(new Dimension(b - scrollBarWidth, 10 + panel3DSize + functieEditorHeight));
    	//panel3D.setSize(panel3DSize, panel3DSize);
    	
    	//knoppenPanel.setLocation(10 + panel3DSize, getLocation().y);
    	
    	//functieEditor.setBounds(10, 10 + panel3DSize, panel3DSize, functieEditorHeight);
    	setNewModel(0, false);
    	
//System.out.println("fid " + figuurIsDemo);    	
    	zetFiguurIsDemo(figuurIsDemo);
    }
    
//	public Image getImage(String name)
//	{	
//		return (Image) images.get(name);
//	}
    
/*	
    public void layoutKnoppenPanel()
    {
    	int currentY = 10;

    	if (zoomOptie || translateOptie)
    	{
    		zoomStandaardButton.setBounds(10, currentY, 23, 23);
    		zoomStandaardButton.setVisible(true);
    		currentY += 29;
    	}
    	else
    	{	zoomStandaardButton.setVisible(false);
    	}

    	if (zoomOptie)
    	{	
    		zoomInButton.setBounds(10, currentY, 21, 21);
    		zoomInButton.setVisible(true);
    		currentY += 26;
		
    		zoomUitButton.setBounds(10, currentY, 21, 21);
    		zoomUitButton.setVisible(true);
    		currentY += 31;
    	}
    	else
    	{	zoomInButton.setVisible(false);
    		zoomUitButton.setVisible(false);
    	}
    	
    	if (translateOptie)
    	{	
    		transPlusButton.setBounds(10, currentY, 21, 21);
    		transPlusButton.setVisible(true);
    		currentY += 26;
		
    		asNaamButton.setBounds(5, currentY, 31, 21);
    		asNaamButton.setVisible(true);
    		currentY += 26;
		
    		transMinButton.setBounds(10, currentY, 21, 21);
    		transMinButton.setVisible(true);
    		currentY += 31;
    	}
    	else
    	{	transPlusButton.setVisible(false);
    		asNaamButton.setVisible(false);
    		transMinButton.setVisible(false);
    	}
    	
    	if (solidDraadKeuzeOptie && (objectType != CURVE))
    	{	
    		solidDraadKeuzeButton.setBounds(10, currentY, 21, 21);
    		solidDraadKeuzeButton.setVisible(true);
    		currentY += 31;
    		if (objectType == FUNCTION)
    		{
    			if (wireFrameG)
    				solidDraadKeuzeButton.setCode("solid");
    			else
    				solidDraadKeuzeButton.setCode("draad");
    		}
    		if (objectType == SURFACE)
    		{
    			if (wireFrameS)
    				solidDraadKeuzeButton.setCode("solid");
    			else
    				solidDraadKeuzeButton.setCode("draad");
    		}
    		
    	}
    	else
    	{	solidDraadKeuzeButton.setVisible(false);
    	}
    	
    	if (finerKeuzeOptie && (objectType == FUNCTION))
    	{	
    		finerPlusButton.setBounds(10, currentY, 21, 21);
    		finerPlusButton.setVisible(true);
    		currentY += 26;
		
    		finerMinButton.setBounds(10, currentY, 21, 21);
    		finerMinButton.setVisible(true);
    		if (xFinerStepsG == 2)
    			finerMinButton.setEnabled(false);
    		else
    			finerMinButton.setEnabled(true);
    		currentY += 31;
    	}
    	else
    	{	finerPlusButton.setVisible(false);
			finerMinButton.setVisible(false);
    	}
    	
    	if (asKeuzeOptie)
    	{	
    		asKeuzeButton.setBounds(10, currentY, 21, 21);
    		asKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
    	else
    	{	asKeuzeButton.setVisible(false);
    	}
    	
    	if (labelKeuzeOptie)
    	{	
    		labelKeuzeButton.setBounds(5, currentY, 31, 21);
    		labelKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
    	else
    	{	labelKeuzeButton.setVisible(false);
    	}
    	
    	if (projectieKeuzeOptie)
    	{	
    		projectieKeuzeButton.setBounds(10, currentY, 21, 21);
    		projectieKeuzeButton.setVisible(true);
    		currentY += 31;
    		if (objectType == FUNCTION)
    		{
    			if (centraleProjG)
    				projectieKeuzeButton.setCode("parallel");
    			else
    				projectieKeuzeButton.setCode("central");
    		}
    		if (objectType == SURFACE)
    		{
    			if (centraleProjS)
    				projectieKeuzeButton.setCode("parallel");
    			else
    				projectieKeuzeButton.setCode("central");
    		}
    		if (objectType == CURVE)
    		{
    			if (centraleProjC)
    				projectieKeuzeButton.setCode("parallel");
    			else
    				projectieKeuzeButton.setCode("central");
    		}
    		
    		
    	}
    	else
    	{	projectieKeuzeButton.setVisible(false);
    	}

    	if (kleurKeuzeOptie && (objectType != CURVE))
    	{	
    		kleurKeuzeButton.setBounds(5, currentY, 31, 21);
    		kleurKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
    	else
    	{	kleurKeuzeButton.setVisible(false);
    	}
    	
    	knoppenPanel.repaint();
    	
    }
*/    
    // changing the model to a new one
    public void setNewModel(int modelCode, boolean reallyNew)
    {
    	
        setProjection(defaultProjection);

        mouseMode = INERT;
       

        //zetHoeken();
			
        currentObjectGroup = makeNewModel(modelCode);        
   	    // HIER!
        //setFilled(false);        
   	    panel3D.initializeModel(currentObjectGroup, false);

        // reset zooming HERE
   	    zoom = defaultZoom;
       	panel3D.setZoomFactor(zoom);        
        
       	panel3D.repaint();
    }    
    
    public void zetHoeken()
    {
//System.out.println("zetH " + objectType);

        if (objectType == FUNCTION)
        {	panel3D.zetHoeken(angleXG, angleZG);
        }
        else if (objectType == SURFACE)
        {	panel3D.zetHoeken(angleXS, angleZS);
        }
        else if (objectType == CURVE)
        {	panel3D.zetHoeken(angleXC, angleZC);
        }
    	
    }

    public void zetStartHoeken(double startX, double startZ)
    {
//System.out.println("zetH " + objectType);

        if (objectType == FUNCTION)
        {	panel3D.zetStartHoeken(startX, startZ);
        }
        else if (objectType == SURFACE)
        {	panel3D.zetStartHoeken(startX, startZ);
        }
        else if (objectType == CURVE)
        {	panel3D.zetStartHoeken(startX, startZ);
        }
    	
    }
    
    public void getHoeken()
    {
//System.out.println("getH " + objectType);    	
        if (objectType == FUNCTION)
        {	angleXG = panel3D.angleX;
        	angleZG = panel3D.angleZ;
        }
        else if (objectType == SURFACE)
        {	angleXS = panel3D.angleX;
    		angleZS = panel3D.angleZ;
        }
        else if (objectType == CURVE)
        {	angleXC = panel3D.angleX;
    		angleZC = panel3D.angleZ;
        }
    	
    }
    
    public ObjectGroup3D makeNewModel(int code)
    {   //Object3D axesModel;
    	//Object3D graph3DModel;
        ObjectGroup3D modelGroup = null;

        // default?
// binnenvulling is onzichtbaar
// maar voor buitenkant toch NZMINFIRST
// is dit ook OK voor filled = false?
        //panel3D.paintType = Object3DContainer.PUREZ;
        
    	axesObject = makeNewAxes();
//System.out.println("axes diam = " + axesObject.getDiameter());    
    	
        if (((objectType == FUNCTION) && !noAxesG) ||
        	((objectType == SURFACE) && !noAxesS) ||
        	((objectType == CURVE) && !noAxesC)
           )
        {	
        	modelGroup = new ObjectGroup3D(axesObject, false);
        	modelGroup.numVertexLabels = axesObject.numVertexLabels;
        }
        
        if (grafiek3DExpressie != null)
        {
        	grafiek3DObject = makeGrafiek3D();
        	grafiek3DObject.modelCode = code;
        	if (modelGroup == null) // geen assen
        	{	
        		modelGroup = new ObjectGroup3D(grafiek3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;
//System.out.println("modelGroup diam = " + modelGroup.getDiameter());        		
        	}
        	else // wel assen
        	{	
        		modelGroup.addObject3D(grafiek3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;
//System.out.println("modelGroup diam = " + modelGroup.getDiameter());        		
        	}
        }
        
        if (surfaceXExpressie != null)
        {	surface3DObject = makeSurface3D();
        	surface3DObject.modelCode = code;
        	if (modelGroup == null) // geen assen
        	{	
        		modelGroup = new ObjectGroup3D(surface3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	else
        	{	
// HIER SNIJDEN MET ASSEN        		
        		modelGroup.addObject3D(surface3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	
        }

        if (curveXExpressie != null)
        {
        	curve3DObject = makeCurve3D();
        	curve3DObject.modelCode = code;
        	if (modelGroup == null) // geen assen
        	{	
        		modelGroup = new ObjectGroup3D(curve3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	else
        	{	
// HIER SNIJDEN MET ASSEN        		
        		modelGroup.addObject3D(curve3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        }
        
        //System.out.println("model-numFacets = " + model.numFacets);        
        //modelGroup = new ObjectGroup3D(model, false);
        //modelGroup.numVertexLabels = axisModel.numVertexLabels;
        return modelGroup;
    }   
    
    public Axes makeNewAxes()
    {	Axes axes = null;
    	
    	if (objectType == FUNCTION)
    		return new Axes(xMinG, xMaxG, xStepG, yMinG, yMaxG, yStepG, zMinG, zMaxG, zStepG, 
    						floorTypeG, labelTypeG, xFinerStepsG, yFinerStepsG);
    	else if (objectType == SURFACE)
    		return new Axes(xMinS, xMaxS, xStepS, yMinS, yMaxS, yStepS, zMinS, zMaxS, zStepS, 
						    floorTypeS, labelTypeS, xFinerStepsS, yFinerStepsS);
    	else if (objectType == CURVE)
    		return new Axes(xMinC, xMaxC, xStepC, yMinC, yMaxC, yStepC, zMinC, zMaxC, zStepC, 
						    floorTypeC, labelTypeC, xFinerStepsC, yFinerStepsC);
    	
    	return axes;	
    }
    
    public Grafiek3D makeGrafiek3D()
    {	
    	
//System.out.println("makeGrafiek3D cfa = " + checkForAsymptotes);    	
    	
    	grafiek3DObject = new Grafiek3D(grafiek3DExpressie, checkForAsymptotes,  
					 				    xMinG, xMaxG, xStepG, yMinG, yMaxG, yStepG, zMinG, zMaxG, zStepG, 
			 					 		varNaamX, varNaamY, xFinerStepsG, yFinerStepsG);    	 
    
    	objectColor = graphColor;
    	
    	
    	if (grafiek3DObject.trimTop)
    	{	
//System.out.println("trimTop");

			grafiek3DObject = (Grafiek3D) Trim.trimObject3D(grafiek3DObject, zMaxG, Trim.ZMAX);
    		
    	} // if trimTop
    	
    	
    	if (grafiek3DObject.trimBottom)
    	{
//System.out.println("trimBottom");    		
    		
			grafiek3DObject = (Grafiek3D) Trim.trimObject3D(grafiek3DObject, zMinG, Trim.ZMIN);
    	}
    
    	grafiek3DObject.setOutlineColor(graphOutlineColor);
    	
    	if (wireFrameG)
    	{	zetDraadFiguur(true, objectType);
    	}
    	if (centraleProjG)
    		setProjection(CENTRALPROJ);
    	else
    		setProjection(PARALLELPROJ);
    	
    	return grafiek3DObject; 
    }
  
    public void zetGrafiek3D(Expressie exp)
    {
//System.out.println("zetGrafiek3D");    	
    	grafiek3DExpressie = exp;
    	if (exp == null)
    		return;
    	
   		setNewModel(0, false);
    }
    
    public Surface3D makeSurface3D()
    {
    	surface3DObject = new Surface3D(surfaceXExpressie, surfaceYExpressie, surfaceZExpressie,
    									checkForAsymptotes, 
    			                        uMin, uMax, uPoints, vMin, vMax, vPoints,
    			                        xMinS, xMaxS, yMinS, yMaxS, zMinS, zMaxS,
    			                        paramNaamU, paramNaamV);
    	
    	objectColor = surfaceColor;
  
    	
    	if (surface3DObject.trimTop)
    	{	
//System.out.println("trimTop");    		

			surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, zMaxS, Trim.ZMAX);    		

    	}
    	if (surface3DObject.trimBottom)
    	{	
//System.out.println("trimBottom");    		

			surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, zMinS, Trim.ZMIN);
    		
    	}
    	if (surface3DObject.trimRight)
    	{	
//System.out.println("trimRight");    		
			
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, xMaxS, Trim.XMAX);
    		
    	}
    	if (surface3DObject.trimLeft)
    	{	
//System.out.println("trimLeft");    		
    		
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, xMinS, Trim.XMIN);
    	}
    	if (surface3DObject.trimBack)
    	{	
//System.out.println("trimBack");    		

    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, yMaxS, Trim.YMAX);

    	}
    	if (surface3DObject.trimFront)
    	{	
//System.out.println("trimFront");    		
    		
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, yMinS, Trim.YMIN);

    	}
   	
    	surface3DObject.setOutlineColor(surfaceOutlineColor);
    	
    	if (wireFrameS)
    		zetDraadFiguur(true, objectType);

    	if (centraleProjS)
    		setProjection(CENTRALPROJ);
    	else
    		setProjection(PARALLELPROJ);
    	
    	return surface3DObject;
    }
    
    
    public Curve3D makeCurve3D()
    {
    	curve3DObject = new Curve3D(curveXExpressie, curveYExpressie, curveZExpressie,
    			checkForAsymptotes, tMin, tMax, tPoints,
                xMinC, xMaxC, yMinC, yMaxC, zMinC, zMaxC,
                paramNaam);

    	objectColor = curveColor;
    
    	
    	if (curve3DObject.trimTop)
    	{	
//System.out.println("trimTop");    		
    		
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, zMaxS, Trim.ZMAX);

    	}
    	if (curve3DObject.trimBottom)
    	{	
//System.out.println("trimBottom");    		
    		
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, zMinS, Trim.ZMIN);    		


    	}
    	if (curve3DObject.trimRight)
    	{	
//System.out.println("trimRight");    		

    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, xMaxS, Trim.XMAX);

    	}
    	if (curve3DObject.trimLeft)
    	{	
//System.out.println("trimLeft");    		

    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, xMinS, Trim.XMIN);
    		
    	}
    	if (curve3DObject.trimBack)
    	{	
//System.out.println("trimBack");    		

    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, yMaxS, Trim.YMAX);
    		
    	}
    	if (curve3DObject.trimFront)
    	{	
//System.out.println("trimFront");    		
    		
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, yMinS, Trim.YMIN);

		}	
		
	    if (centraleProjC)
	    	setProjection(CENTRALPROJ);
	    else
	    		setProjection(PARALLELPROJ);
    	
    	return curve3DObject;

    }
    
    public void zetSurface3D(Expressie xExp, Expressie yExp, Expressie zExp, 
    						 double uMi, double uMa, int uPo,
    						 double vMi, double vMa, int vPo)
    {	if (xExp == null)
    	{	surfaceXExpressie = xExp;
    		return;
    	}
    	else
    	{	surfaceXExpressie = xExp;
    		surfaceYExpressie = yExp;
    		surfaceZExpressie = zExp;
    		uMin = uMi;
    		uMax = uMa;
    		uPoints = uPo;
    		vMin = vMi;
    		vMax = vMa;
    		vPoints = vPo;
    		
    	}

   		setNewModel(0, false);
    
    }
    
    public void zetCurve3D(Expressie xExp, Expressie yExp, Expressie zExp, 
			 			   double tMi, double tMa, int tPo)
    {	if (xExp == null)
    	{	curveXExpressie = xExp;
    		return;
    	}
    	else
    	{	curveXExpressie = xExp;
    		curveYExpressie = yExp;
    		curveZExpressie = zExp;
    		tMin = tMi;
    		tMax = tMa;
    		tPoints = tPo;

    	}

   		setNewModel(0, false);

    }
    
    
    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            defaultProjection = CENTRALPROJ;
        if (proj == PARALLELPROJ)    
            defaultProjection = PARALLELPROJ;    
        panel3D.setProjection(defaultProjection);
    }

/*    
    public void setLetters(boolean b)
    {   letters = b;
        panel3D.repaint();
    }
*/    
    
    
    public void zoomStandaard(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		xMinG = xMinBegin;
    		xMaxG = xMaxBegin;
    		xStepG = xStepBegin;
    		yMinG = yMinBegin;
    		yMaxG = yMaxBegin;
    		yStepG = yStepBegin;
    		zMinG = zMinBegin;
    		zMaxG = zMaxBegin;
    		zStepG = zStepBegin;
    		xFinerStepsG = xFinerStepsBegin;
    		yFinerStepsG = yFinerStepsBegin;
    		
    		//finerMinButton.setEnabled(false);
    	
    		zoomFactorG = 0;
    		translateXFactorG = 0;
    		translateYFactorG = 0;
    		translateZFactorG = 0;
    		finerFactorG = 0;
    		
    		angleXG = Object3DContainer.angleXStart;
    		angleZG = Object3DContainer.angleZStart;
    	}
    	else if (objectType == SURFACE)
    	{	
    		xMinS = xMinBegin;
    		xMaxS = xMaxBegin;
    		xStepS = xStepBegin;
    		yMinS = yMinBegin;
    		yMaxS = yMaxBegin;
    		yStepS = yStepBegin;
    		zMinS = zMinBegin;
    		zMaxS = zMaxBegin;
    		zStepS = zStepBegin;
    		xFinerStepsS = xFinerStepsBegin;
    		yFinerStepsS = yFinerStepsBegin;
    	
    		zoomFactorS = 0;
    		translateXFactorS = 0;
    		translateYFactorS = 0;
    		translateZFactorS = 0;
    		
    		angleXS = Object3DContainer.angleXStart;
    		angleZS = Object3DContainer.angleZStart;
    		
    	}
    	else if (objectType == CURVE)
    	{	
    		xMinC = xMinBegin;
    		xMaxC = xMaxBegin;
    		xStepC = xStepBegin;
    		yMinC = yMinBegin;
    		yMaxC = yMaxBegin;
    		yStepC = yStepBegin;
    		zMinC = zMinBegin;
    		zMaxC = zMaxBegin;
    		zStepC = zStepBegin;
    		xFinerStepsC = xFinerStepsBegin;
    		yFinerStepsC = yFinerStepsBegin;
    	
    		zoomFactorC = 0;
    		translateXFactorC = 0;
    		translateYFactorC = 0;
    		translateZFactorC = 0;
    		
    		angleXC = Object3DContainer.angleXStart;
    		angleZC = Object3DContainer.angleZStart;
    		
    	}
    	
    	if (newModel)
    	{	
    		zetHoeken();
    		setNewModel(0, false);
    	
    	}
    }
    
    public void zoomIn(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		double centerX = (xMinG + xMaxG) / 2;
    		double centerY = (yMinG + yMaxG) / 2;
    		double centerZ = (zMinG + zMaxG) / 2;
    		xMaxG = centerX + (xMaxG - centerX) / 2;
    		xMinG = centerX - (centerX - xMinG) / 2;
    		yMaxG = centerY + (yMaxG - centerY) / 2;
    		yMinG = centerY - (centerY - yMinG) / 2;
    		zMaxG = centerZ + (zMaxG - centerZ) / 2;
    		zMinG = centerZ - (centerZ - zMinG) / 2;
    		xStepG /= 2;
    		yStepG /= 2;
    		zStepG /= 2;
    		zoomFactorG++;

    	}
    	else if (objectType == SURFACE)
    	{	
    		double centerX = (xMinS + xMaxS) / 2;
    		double centerY = (yMinS + yMaxS) / 2;
    		double centerZ = (zMinS + zMaxS) / 2;
    		xMaxS = centerX + (xMaxS - centerX) / 2;
    		xMinS = centerX - (centerX - xMinS) / 2;
    		yMaxS = centerY + (yMaxS - centerY) / 2;
    		yMinS = centerY - (centerY - yMinS) / 2;
    		zMaxS = centerZ + (zMaxS - centerZ) / 2;
    		zMinS = centerZ - (centerZ - zMinS) / 2;
    		xStepS /= 2;
    		yStepS /= 2;
    		zStepS /= 2;
    		zoomFactorS++;

    	}
    	else if (objectType == CURVE)
    	{	
    		double centerX = (xMinC + xMaxC) / 2;
    		double centerY = (yMinC + yMaxC) / 2;
    		double centerZ = (zMinC + zMaxC) / 2;
    		xMaxC = centerX + (xMaxC - centerX) / 2;
    		xMinC = centerX - (centerX - xMinC) / 2;
    		yMaxC = centerY + (yMaxC - centerY) / 2;
    		yMinC = centerY - (centerY - yMinC) / 2;
    		zMaxC = centerZ + (zMaxC - centerZ) / 2;
    		zMinC = centerZ - (centerZ - zMinC) / 2;
    		xStepC /= 2;
    		yStepC /= 2;
    		zStepC /= 2;
    		zoomFactorC++;

    	}
    	

    	if (newModel)    	
		{	setNewModel(0, false);
		}
    	
    }
    
    public void zoomUit(boolean newModel, int objectType)
    {
    	
    	if (objectType == FUNCTION)
    	{	
    		double centerX = (xMinG + xMaxG) / 2;
    		double centerY = (yMinG + yMaxG) / 2;
    		double centerZ = (zMinG + zMaxG) / 2;
    		xMaxG = centerX + (xMaxG - centerX) * 2;
    		xMinG = centerX - (centerX - xMinG) * 2;
    		yMaxG = centerY + (yMaxG - centerY) * 2;
    		yMinG = centerY - (centerY - yMinG) * 2;
    		zMaxG = centerZ + (zMaxG - centerZ) * 2;
    		zMinG = centerZ - (centerZ - zMinG) * 2;
    		xStepG *= 2;
    		yStepG *= 2;
    		zStepG *= 2;
    		zoomFactorG--;
    	}
    	else if (objectType == SURFACE)
    	{	
    		double centerX = (xMinS + xMaxS) / 2;
    		double centerY = (yMinS + yMaxS) / 2;
    		double centerZ = (zMinS + zMaxS) / 2;
    		xMaxS = centerX + (xMaxS - centerX) * 2;
    		xMinS = centerX - (centerX - xMinS) * 2;
    		yMaxS = centerY + (yMaxS - centerY) * 2;
    		yMinS = centerY - (centerY - yMinS) * 2;
    		zMaxS = centerZ + (zMaxS - centerZ) * 2;
    		zMinS = centerZ - (centerZ - zMinS) * 2;
    		xStepS *= 2;
    		yStepS *= 2;
    		zStepS *= 2;
    		zoomFactorS--;
    	}
    	else if (objectType == CURVE)
    	{	
    		double centerX = (xMinC + xMaxC) / 2;
    		double centerY = (yMinC + yMaxC) / 2;
    		double centerZ = (zMinC + zMaxC) / 2;
    		xMaxC = centerX + (xMaxC - centerX) * 2;
    		xMinC = centerX - (centerX - xMinC) * 2;
    		yMaxC = centerY + (yMaxC - centerY) * 2;
    		yMinC = centerY - (centerY - yMinC) * 2;
    		zMaxC = centerZ + (zMaxC - centerZ) * 2;
    		zMinC = centerZ - (centerZ - zMinC) * 2;
    		xStepC *= 2;
    		yStepC *= 2;
    		zStepC *= 2;
    		zoomFactorC--;
    	}
    	
    	
		if (newModel)    	
		{	setNewModel(0, false);
		}	
    	
    }
    
    public void transPlusX(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		xMinG += xStepG;
    		xMaxG += xStepG;
    		translateXFactorG++;
    	}	
    	else if (objectType == SURFACE)
    	{	
    		xMinS += xStepS;
    		xMaxS += xStepS;
    		translateXFactorS++;
    	}	
    	else if (objectType == CURVE)
    	{	
    		xMinC += xStepC;
    		xMaxC += xStepC;
    		translateXFactorC++;
    	}	
    	
    	if (newModel)    	
    	{	setNewModel(0, false);
    	}	
    	
    	
    }
    public void transMinX(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		xMinG -= xStepG;
    		xMaxG -= xStepG;
    		translateXFactorG--;
    	}
    	else if (objectType == SURFACE)
    	{	
    		xMinS -= xStepS;
    		xMaxS -= xStepS;
    		translateXFactorS--;
    	}
    	else if (objectType == CURVE)
    	{	
    		xMinC -= xStepC;
    		xMaxC -= xStepC;
    		translateXFactorC--;
    	}
    	
    	if (newModel)		
    	{	setNewModel(0, false);
    	}	
    	
    }
    public void transPlusY(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		yMinG += yStepG;
    		yMaxG += yStepG;
    		translateYFactorG++;
    	}	
    	else if (objectType == SURFACE)
    	{	
    		yMinS += yStepS;
    		yMaxS += yStepS;
    		translateYFactorS++;
    	}	
    	else if (objectType == CURVE)
    	{	
    		yMinC += yStepC;
    		yMaxC += yStepC;
    		translateYFactorC++;
    	}	
    	
    	if (newModel)		
		{	setNewModel(0, false);
		}		
    	
    }
    public void transMinY(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		yMinG -= yStepG;
    		yMaxG -= yStepG;
    		translateYFactorG--;
    	}
    	else if (objectType == SURFACE)
    	{	
    		yMinS -= yStepS;
    		yMaxS -= yStepS;
    		translateYFactorS--;
    	}
    	else if (objectType == CURVE)
    	{	
    		yMinC -= yStepC;
    		yMaxC -= yStepC;
    		translateYFactorC--;
    	}
    	
   		if (newModel)		
   		{	setNewModel(0, false);
		}
    	
    }
    public void transPlusZ(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		zMinG += zStepG;
    		zMaxG += zStepG;
    		translateZFactorG++;
    	}
    	else if (objectType == SURFACE)
    	{	
    		zMinS += zStepS;
    		zMaxS += zStepS;
    		translateZFactorS++;
    	}	
    	else if (objectType == CURVE)
    	{	
    		zMinC += zStepC;
    		zMaxC += zStepC;
    		translateZFactorC++;
    	}	
    	
    	
    	if (newModel)
    	{	setNewModel(0, false);
    	}
    	
    }
    public void transMinZ(boolean newModel, int objectType)
    {	
    	if (objectType == FUNCTION)
    	{	
    		zMinG -= zStepG;
    		zMaxG -= zStepG;
    		translateZFactorG--;
    	}	
    	else if (objectType == SURFACE)
    	{	
    		zMinS -= zStepS;
    		zMaxS -= zStepS;
    		translateZFactorS--;
    	}
    	else if (objectType == CURVE)
    	{	
    		zMinC -= zStepC;
    		zMaxC -= zStepC;
    		translateZFactorC--;
    	}
    	
    	if (newModel)
    	{	setNewModel(0, false);
    	}
    }
    
    public void zetDraadFiguur(int objectType)
    {
    	if (objectType == FUNCTION)
    	{	zetDraadFiguur(wireFrameG, objectType);
    	}
    	else if (objectType == SURFACE)
    	{	zetDraadFiguur(wireFrameS, objectType);
    	}
    	
    }
    
    public void zetDraadFiguur(boolean b, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		wireFrameG = b;
    		if (b)
    		{	if (grafiek3DObject != null)
    			{	grafiek3DObject.setFilled(false);
    				grafiek3DObject.setOutlineColor(wireFrameColor);
    			}
    		}
    		else
    		{	if (grafiek3DObject != null)
				{	grafiek3DObject.setFilled(true);
					grafiek3DObject.setOutlineColor(graphOutlineColor);
				}
    		}
    	}
    	else if (objectType == SURFACE)
    	{	
    		wireFrameS = b;
    		if (b)
    		{	if (surface3DObject != null)
    			{	surface3DObject.setFilled(false);
    				surface3DObject.setOutlineColor(wireFrameColor);
    			}
    		}
    		else
    		{	if (surface3DObject != null)
    			{	surface3DObject.setFilled(true);
    				surface3DObject.setOutlineColor(surfaceOutlineColor);
    			}
    		}
    	}
    		
    	panel3D.repaint();
    }

    public void zetVulKleur(CssColor fc, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		graphColor = fc;
    		objectColor = fc;
    		if (grafiek3DObject != null)
    		{	grafiek3DObject.setFillColor(graphColor);
    		}
    	}
    	else if (objectType == SURFACE)
    	{	
    		surfaceColor = fc;
    		objectColor = fc; 
    		if (surface3DObject != null)
    		{	surface3DObject.setFillColor(surfaceColor);
   			}
    	}
    		
    	//panel3D.repaint();
    	//knoppenPanel.repaint();
    }
    
    public void zetFijner(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		
System.out.println("fijner");

    		xFinerStepsG += 1;
    		yFinerStepsG += 1;
    		finerFactorG++;
    		
    		//if (finerFactorG > 0)
    		//	owner.groverButton.setEnabled(true);
    	
    		if (newModel)
    		{	setNewModel(0, false);
    		}
    	}	
    }
    
    public void zetGrover(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
System.out.println("grover");    		
    		
    		if (finerFactorG > 0)
    		{	
    			xFinerStepsG -= 1;
    			yFinerStepsG -= 1;
    			finerFactorG--;
    		}
    		
    		//if (finerFactorG == 0)
    		//	owner.groverButton.setEnabled(false);
    	
    		if (newModel)
    		{	setNewModel(0, false);
    		}
    	}	
    }
    public void zetGeenAssen(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		noAxesG = true;
    	}
    	else if (objectType == SURFACE)
    	{	
    		noAxesS = true;
    	}
    	else if (objectType == CURVE)
    	{	
    		noAxesC = true;
    	}
    	if (newModel)
    		setNewModel(0, false);
    }
    
    public void zetxyzAs(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		noAxesG = false;
    		floorTypeG = NOFLOOR;
    	}
    	else if (objectType == SURFACE)
    	{	
    		noAxesS = false;
    		floorTypeS = NOFLOOR;
    	}
    	else if (objectType == CURVE)
    	{	
    		noAxesC = false;
    		floorTypeC = NOFLOOR;
    	}
    	if (newModel)
    		setNewModel(0, false);
    }
    
    public void zetxyVloer(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		noAxesG = false;
    		floorTypeG = TRANSFLOOR;
    	}
    	else if (objectType == SURFACE)
    	{	
    		noAxesS = false;
    		floorTypeS = TRANSFLOOR;
    	}
    	else if (objectType == CURVE)
    	{	
    		noAxesC = false;
    		floorTypeC = TRANSFLOOR;
    	}
    	
    	
    	if (newModel)
    		setNewModel(0, false);
    }
    
    
    public void zetLabelKeuze(boolean newModel, int type, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		labelTypeG = type;
    	}
    	else if (objectType == SURFACE)
    	{	
    		labelTypeS = type;
    	}
    	else if (objectType == CURVE)
    	{	
    		labelTypeS = type;
    	}
    	if (newModel)
    		setNewModel(0, false);
    }

    public void zetCentraleProjectie(boolean b, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		centraleProjG = b;
    		if (b)
    			setProjection(CENTRALPROJ);
    		else
    			setProjection(PARALLELPROJ);
    		
    		
    	}
    	else if (objectType == SURFACE)
    	{	
    		centraleProjS = b;
    		if (b)
    			setProjection(CENTRALPROJ);
    		else
    			setProjection(PARALLELPROJ);
    		
    	}
    	else if (objectType == CURVE)
    	{	
    		centraleProjC = b;
    		if (b)
    			setProjection(CENTRALPROJ);
    		else
    			setProjection(PARALLELPROJ);
    		
    	}
    	
    	//panel3D.repaint();
    	
    	
    }
    
	public void zetZoomOptie(boolean b)
	{	zoomOptie = b;

		//layoutKnoppenPanel();
	}
	
	public void zetTranslateOptie(boolean b)
	{	translateOptie = b;
		//layoutKnoppenPanel();
	}
	
	public void zetSolidDraadKeuzeOptie(boolean b)
	{	solidDraadKeuzeOptie = b;
		//layoutKnoppenPanel();
	}
	
	public void zetFinerKeuzeOptie(boolean b)
	{	finerKeuzeOptie = b;
		//layoutKnoppenPanel();
	}
	
	public void zetAsKeuzeOptie(boolean b)
	{	asKeuzeOptie = b;
		//layoutKnoppenPanel();
		
	}
	
	public void zetLabelKeuzeOptie(boolean b)
	{	labelKeuzeOptie = b;
		//layoutKnoppenPanel();
		
	}

	public void zetProjectieKeuzeOptie(boolean b)
	{	projectieKeuzeOptie = b;
		//layoutKnoppenPanel();
		
	}

	public void zetKleurKeuzeOptie(boolean b)
	{	kleurKeuzeOptie = b;
		//layoutKnoppenPanel();
		
	}
	
// SCROLLPANEL ERTUSSENUIT 	
	public void zetFiguurIsDemo(boolean b)
	{	figuurIsDemo = b;
		if (figuurIsDemo)
		{
			//panel3D.setBounds(0, 0, panel3DSize, panel3DSize);
			//panel3D.setBordered(false);
			
		}
		else
		{
			//panel3D.setBounds(10, 10, panel3DSize, panel3DSize);
			//panel3D.setBordered(true);
		}
		setNewModel(0, false);
	}
	
	// doorsturen naar FunctieEditor
	public void zetFunctieTypeKeuze(boolean b)
	{
		//functieEditor.zetFunctieTypeKeuze(b);
	}
    
	public void zetVoorbeeldenEnabled(boolean b)
	{
		//functieEditor.zetVoorbeeldenEnabled(b);
	}
	
	public void zetGrafiekVoorbeeld(GrafiekVoorbeeld gv)
	{
		checkForAsymptotes = gv.checkForAsymptotes;
		
		zoomStandaard(false, FUNCTION);

		angleXG = gv.angleXG;
		angleZG = gv.angleZG;
		zetHoeken();
		if (gv.zoomFactorG > 0)
		{	for (int zUitCnt = 0; zUitCnt < gv.zoomFactorG; zUitCnt++)
				zoomUit(false, FUNCTION);
		}
		if (gv.zoomFactorG < 0)
		{	for (int zInCnt = gv.zoomFactorG; zInCnt < 0; zInCnt++)
			zoomIn(false, FUNCTION);
		}
		if (gv.translateXFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < gv.translateXFactorG; tPlusCnt++)
				transPlusX(false, FUNCTION);
		}
		if (gv.translateXFactorG < 0)
		{	for (int tMinCnt = gv.translateXFactorG; tMinCnt < 0; tMinCnt++)
				transMinX(false, FUNCTION);
		}
		if (gv.translateYFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < gv.translateYFactorG; tPlusCnt++)
				transPlusY(false, FUNCTION);
		}
		if (gv.translateYFactorG < 0)
		{	for (int tMinCnt = gv.translateYFactorG; tMinCnt < 0; tMinCnt++)
				transMinY(false, FUNCTION);
		}
		if (gv.translateZFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < gv.translateZFactorG; tPlusCnt++)
				transPlusZ(false, FUNCTION);
		}
		if (gv.translateZFactorG < 0)
		{	for (int tMinCnt = gv.translateZFactorG; tMinCnt < 0; tMinCnt++)
				transMinZ(false, FUNCTION);
		}
		wireFrameG = gv.wireFrameG;

		if (gv.finerFactorG > 0)
		{	for (int fPlusCnt = 0; fPlusCnt < gv.finerFactorG; fPlusCnt++)
				zetFijner(false, FUNCTION);
		}
		if (gv.finerFactorG < 0)
		{	for (int fMinCnt = gv.finerFactorG; fMinCnt < 0; fMinCnt++)
				zetGrover(false, FUNCTION);
		}
		
		noAxesG = gv.noAxesG;
		floorTypeG = gv.floorTypeG;

		labelTypeG = gv.labelTypeG;
		
		centraleProjG = gv.centraleProjG;
		zetCentraleProjectie(centraleProjG, FUNCTION);
		
		zetVulKleur(gv.graphColor, FUNCTION);
		
		//layoutKnoppenPanel();
		
	}

	public void zetOppervlakVoorbeeld(OppervlakVoorbeeld ov)
	{
		checkForAsymptotes = ov.checkForAsymptotes;
		
		zoomStandaard(false, SURFACE);
		
		angleXS = ov.angleXS;
		angleZS = ov.angleZS;
		zetHoeken();
		if (ov.zoomFactorS > 0)
		{	for (int zUitCnt = 0; zUitCnt < ov.zoomFactorS; zUitCnt++)
				zoomUit(false, SURFACE);
		}
		if (ov.zoomFactorS < 0)
		{	for (int zInCnt = ov.zoomFactorS; zInCnt < 0; zInCnt++)
			zoomIn(false, SURFACE);
		}
		if (ov.translateXFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < ov.translateXFactorS; tPlusCnt++)
				transPlusX(false, SURFACE);
		}
		if (ov.translateXFactorS < 0)
		{	for (int tMinCnt = ov.translateXFactorS; tMinCnt < 0; tMinCnt++)
				transMinX(false, SURFACE);
		}
		if (ov.translateYFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < ov.translateYFactorS; tPlusCnt++)
				transPlusY(false, SURFACE);
		}
		if (ov.translateYFactorS < 0)
		{	for (int tMinCnt = ov.translateYFactorS; tMinCnt < 0; tMinCnt++)
				transMinY(false, SURFACE);
		}
		if (ov.translateZFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < ov.translateZFactorS; tPlusCnt++)
				transPlusZ(false, SURFACE);
		}
		if (ov.translateZFactorS < 0)
		{	for (int tMinCnt = ov.translateZFactorS; tMinCnt < 0; tMinCnt++)
				transMinZ(false, SURFACE);
		}
		wireFrameS = ov.wireFrameS;

		noAxesS = ov.noAxesS;
		floorTypeS = ov.floorTypeS;

		labelTypeS = ov.labelTypeS;
		
		centraleProjS = ov.centraleProjS;
		zetCentraleProjectie(centraleProjS, SURFACE);
		
		zetVulKleur(ov.surfaceColor, SURFACE);
		
		//layoutKnoppenPanel();
		
	}
	
	public void zetKrommeVoorbeeld(KrommeVoorbeeld kv)
	{
		checkForAsymptotes = kv.checkForAsymptotes;
		
		zoomStandaard(false, CURVE);
		
		angleXC = kv.angleXC;
		angleZC = kv.angleZC;
		zetHoeken();
		
		if (kv.zoomFactorC > 0)
		{	for (int zUitCnt = 0; zUitCnt < kv.zoomFactorC; zUitCnt++)
				zoomUit(false, CURVE);
		}
		if (kv.zoomFactorC < 0)
		{	for (int zInCnt = kv.zoomFactorC; zInCnt < 0; zInCnt++)
			zoomIn(false, CURVE);
		}
		if (kv.translateXFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < kv.translateXFactorC; tPlusCnt++)
				transPlusX(false, CURVE);
		}
		if (kv.translateXFactorC < 0)
		{	for (int tMinCnt = kv.translateXFactorC; tMinCnt < 0; tMinCnt++)
				transMinX(false, CURVE);
		}
		if (kv.translateYFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < kv.translateYFactorC; tPlusCnt++)
				transPlusY(false, CURVE);
		}
		if (kv.translateYFactorC < 0)
		{	for (int tMinCnt = kv.translateYFactorC; tMinCnt < 0; tMinCnt++)
				transMinY(false, CURVE);
		}
		if (kv.translateZFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < kv.translateZFactorC; tPlusCnt++)
				transPlusZ(false, CURVE);
		}
		if (kv.translateZFactorC < 0)
		{	for (int tMinCnt = kv.translateZFactorC; tMinCnt < 0; tMinCnt++)
				transMinZ(false, CURVE);
		}

		noAxesC = kv.noAxesC;
		floorTypeC = kv.floorTypeC;

		labelTypeC = kv.labelTypeC;
		
		centraleProjC = kv.centraleProjC;
		zetCentraleProjectie(centraleProjC, CURVE);
		
		//layoutKnoppenPanel();
		
	}
/*	
    public void actionPerformed(ActionEvent e)
    {	if (e.getActionCommand().equals("focus"))
    		return;
    
    	if (e.getActionCommand().equals("knop1"))
    		return;
    	
    	if (e.getSource() == zoomStandaardButton)
    	{	zoomStandaard(true, objectType);
    	}
    	else if (e.getSource() == zoomInButton) 
    	{	zoomIn(true, objectType);	
    	}
    	else if (e.getSource() == zoomUitButton) 
    	{	zoomUit(true, objectType);
    	}
    	else if (e.getSource() == transPlusButton) 
    	{	if (asNaamButton.getCode().equals("xasnaam"))
			{	transPlusX(true, objectType);
			}
			else if (asNaamButton.getCode().equals("yasnaam"))
			{	transPlusY(true, objectType);
			}
			else if (asNaamButton.getCode().equals("zasnaam"))
			{	transPlusZ(true, objectType);
			}
    	}
    	else if (e.getSource() == transMinButton) 
    	{	if (asNaamButton.getCode().equals("xasnaam"))
			{	transMinX(true, objectType);
			}
			else if (asNaamButton.getCode().equals("yasnaam"))
			{	transMinY(true, objectType);
			}
			else if (asNaamButton.getCode().equals("zasnaam"))
			{	transMinZ(true, objectType);
			}
    	}
    	else if (e.getSource() == asNaamButton) 
    	{	if (asNaamButton.getCode().equals("xasnaam"))
    		{	asNaamButton.setCode("yasnaam");
    		}
    		else if (asNaamButton.getCode().equals("yasnaam"))
    		{	asNaamButton.setCode("zasnaam");
    		}
    		else if (asNaamButton.getCode().equals("zasnaam"))
    		{	asNaamButton.setCode("xasnaam");
    		}
    	}
    	else if (e.getSource() == solidDraadKeuzeButton)
    	{
    		if (solidDraadKeuzeButton.getCode().equals("solid"))
    		{	solidDraadKeuzeButton.setCode("draad");
    			zetDraadFiguur(false, objectType);    			
    		}
    		else if (solidDraadKeuzeButton.getCode().equals("draad"))
    		{	solidDraadKeuzeButton.setCode("solid");
				zetDraadFiguur(true, objectType);
    		}
    	}
    	else if (e.getSource() == finerPlusButton)
    	{
    		zetFijner(true, objectType);
    	}
    	else if (e.getSource() == finerMinButton)
    	{
    		zetGrover(true, objectType);
    	}
    	
    	else if (e.getSource() == asKeuzeButton) 
    	{
			int width = 93;
			if (assenPopup.getSize().width != 0)
				width = assenPopup.getSize().width;

    		assenPopup.show(this, asKeuzeButton.getLocation().x + knoppenPanel.getLocation().x - width, 
    				              asKeuzeButton.getLocation().y);
    	}
    	else if (e.getSource() == labelKeuzeButton) 
    	{
			int width = 91;
			if (labelsPopup.getSize().width != 0)
				width = labelsPopup.getSize().width;

			labelsPopup.show(this, labelKeuzeButton.getLocation().x + knoppenPanel.getLocation().x - width, 
								   labelKeuzeButton.getLocation().y);
    		
    	}
    	else if (e.getSource() == projectieKeuzeButton)
    	{
    		if (projectieKeuzeButton.getCode().equals("central"))
    		{	projectieKeuzeButton.setCode("parallel");
    			zetCentraleProjectie(true, objectType);    			
    		}
    		else if (projectieKeuzeButton.getCode().equals("parallel"))
    		{	projectieKeuzeButton.setCode("central");
    			zetCentraleProjectie(false, objectType);
    		}
    	}
       	else if (e.getSource() == kleurKeuzeButton) 
    	{
			int width = 61;
			if (kleurenPopup.getSize().width != 0)
				width = kleurenPopup.getSize().width;

			kleurenPopup.show(this, kleurKeuzeButton.getLocation().x + knoppenPanel.getLocation().x - width, 
								    kleurKeuzeButton.getLocation().y);
    		
    	}
    	
    	else if ((e.getSource() instanceof JMenuItem) && 
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("geenAssenTekst")))
    	{
    		zetGeenAssen(true, objectType);
    	}
    	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("xyzAsTekst")))
    	{
    		zetxyzAs(true, objectType);
    	}
    	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("xyVloerTekst")))
    	{
    		zetxyVloer(true, objectType);
    	}
    	else if ((e.getSource() instanceof JMenuItem) &&
    	        ((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("geenLabelsTekst")))
    	{
    		zetLabelKeuze(true, NOLABELS, objectType);
    	}
    	else if ((e.getSource() instanceof JMenuItem) &&
    	        ((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("eindLabelsTekst")))
    	{
    		zetLabelKeuze(true, ENDLABELS, objectType);
    	}
    	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("alleLabelsTekst")))
    	{
    		zetLabelKeuze(true, ALLLABELS, objectType);
    	}
      	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("geelTekst")))
    	{
    		zetVulKleur(transYellow, objectType);
    	}
      	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("cyanTekst")))
    	{
    		zetVulKleur(transCyan, objectType);
    	}
      	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("magentaTekst")))
    	{
    		zetVulKleur(transMagenta, objectType);
    	}
      	else if ((e.getSource() instanceof JMenuItem) &&
    			((JMenuItem) e.getSource()).getText().equals(Grafiek3DTest.rb.getString("groenTekst")))
    	{
    		zetVulKleur(transGreen, objectType);
    	}
      
    }
*/    
    public Object3D cutObjectGroup(ObjectGroup3D ob, Plane3D plane)
    {   
        Object3D start = ob.leftMostLeaf().deepCopy();
        start.setVisible(true);
        start.setFilled(ob.filled);
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);
        startGroup.filled = start.filled;
        startGroup.visible = start.visible;
        startGroup.numVertexLabels = start.numVertexLabels;
        startGroup.fixFacetArray();
        ObjectWithPlane owp = new ObjectWithPlane(startGroup, plane.support,
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2),
            0, false);
        owp.fixFacetArray();    

        Object3D left = new EmptyObject3D();
        Object3D right = new EmptyObject3D();
        
        Vector3D insideVertex = null;
        
        if (start instanceof Grafiek3D)
        {
        	left = new Grafiek3D();
        	((Grafiek3D) left).trimTop = ((Grafiek3D) start).trimTop;
        	((Grafiek3D) left).trimBottom = ((Grafiek3D) start).trimBottom;
        	((Grafiek3D) left).topMaxVertex  = Vector3D.copyVector3D(((Grafiek3D) start).topMaxVertex);
        	((Grafiek3D) left).bottomMinVertex  = Vector3D.copyVector3D(((Grafiek3D) start).bottomMinVertex);
        	((Grafiek3D) left).insideVertex  = Vector3D.copyVector3D(((Grafiek3D) start).insideVertex);
        	right = new Grafiek3D();
        	((Grafiek3D) right).trimTop = ((Grafiek3D) start).trimTop;
        	((Grafiek3D) right).trimBottom = ((Grafiek3D) start).trimBottom;
        	((Grafiek3D) right).topMaxVertex  = Vector3D.copyVector3D(((Grafiek3D) start).topMaxVertex);
        	((Grafiek3D) right).bottomMinVertex  = Vector3D.copyVector3D(((Grafiek3D) start).bottomMinVertex);
        	((Grafiek3D) right).insideVertex  = Vector3D.copyVector3D(((Grafiek3D) start).insideVertex);
        	
        	insideVertex = Vector3D.copyVector3D(((Grafiek3D) start).insideVertex);
        	
        }
        
        if (start instanceof Surface3D)
        {
        	left = new Surface3D();
        	((Surface3D) left).trimTop = ((Surface3D) start).trimTop;
        	((Surface3D) left).trimBottom = ((Surface3D) start).trimBottom;
        	((Surface3D) left).trimFront = ((Surface3D) start).trimFront;
        	((Surface3D) left).trimBack = ((Surface3D) start).trimBack;
        	((Surface3D) left).trimLeft = ((Surface3D) start).trimLeft;
        	((Surface3D) left).trimRight = ((Surface3D) start).trimRight;
        	
        	((Surface3D) left).topMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).topMaxVertex);
        	((Surface3D) left).bottomMinVertex  = Vector3D.copyVector3D(((Surface3D) start).bottomMinVertex);
        	((Surface3D) left).frontMinVertex  = Vector3D.copyVector3D(((Surface3D) start).frontMinVertex);
        	((Surface3D) left).backMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).backMaxVertex);
        	((Surface3D) left).leftMinVertex  = Vector3D.copyVector3D(((Surface3D) start).leftMinVertex);
        	((Surface3D) left).rightMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).rightMaxVertex);
        	
        	((Surface3D) left).insideVertex  = Vector3D.copyVector3D(((Surface3D) start).insideVertex);
        	
        	right = new Surface3D();
        	((Surface3D) right).trimTop = ((Surface3D) start).trimTop;
        	((Surface3D) right).trimBottom = ((Surface3D) start).trimBottom;
        	((Surface3D) right).trimFront = ((Surface3D) start).trimFront;
        	((Surface3D) right).trimBack = ((Surface3D) start).trimBack;
        	((Surface3D) right).trimLeft = ((Surface3D) start).trimLeft;
        	((Surface3D) right).trimRight = ((Surface3D) start).trimRight;
        	
        	((Surface3D) right).topMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).topMaxVertex);
        	((Surface3D) right).bottomMinVertex  = Vector3D.copyVector3D(((Surface3D) start).bottomMinVertex);
        	((Surface3D) right).frontMinVertex  = Vector3D.copyVector3D(((Surface3D) start).frontMinVertex);
        	((Surface3D) right).backMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).backMaxVertex);
        	((Surface3D) right).leftMinVertex  = Vector3D.copyVector3D(((Surface3D) start).leftMinVertex);
        	((Surface3D) right).rightMaxVertex  = Vector3D.copyVector3D(((Surface3D) start).rightMaxVertex);
        	
        	((Surface3D) right).insideVertex  = Vector3D.copyVector3D(((Surface3D) start).insideVertex);
        	
        	insideVertex = Vector3D.copyVector3D(((Surface3D) start).insideVertex);
        }

        if (start instanceof Curve3D)
        {
        	left = new Curve3D();
        	((Curve3D) left).trimTop = ((Curve3D) start).trimTop;
        	((Curve3D) left).trimBottom = ((Curve3D) start).trimBottom;
        	((Curve3D) left).trimFront = ((Curve3D) start).trimFront;
        	((Curve3D) left).trimBack = ((Curve3D) start).trimBack;
        	((Curve3D) left).trimLeft = ((Curve3D) start).trimLeft;
        	((Curve3D) left).trimRight = ((Curve3D) start).trimRight;
        	
        	((Curve3D) left).topMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).topMaxVertex);
        	((Curve3D) left).bottomMinVertex  = Vector3D.copyVector3D(((Curve3D) start).bottomMinVertex);
        	((Curve3D) left).frontMinVertex  = Vector3D.copyVector3D(((Curve3D) start).frontMinVertex);
        	((Curve3D) left).backMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).backMaxVertex);
        	((Curve3D) left).leftMinVertex  = Vector3D.copyVector3D(((Curve3D) start).leftMinVertex);
        	((Curve3D) left).rightMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).rightMaxVertex);
        	
        	((Curve3D) left).insideVertex  = Vector3D.copyVector3D(((Curve3D) start).insideVertex);
        	
        	right = new Curve3D();
        	((Curve3D) right).trimTop = ((Curve3D) start).trimTop;
        	((Curve3D) right).trimBottom = ((Curve3D) start).trimBottom;
        	((Curve3D) right).trimFront = ((Curve3D) start).trimFront;
        	((Curve3D) right).trimBack = ((Curve3D) start).trimBack;
        	((Curve3D) right).trimLeft = ((Curve3D) start).trimLeft;
        	((Curve3D) right).trimRight = ((Curve3D) start).trimRight;
        	
        	((Curve3D) right).topMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).topMaxVertex);
        	((Curve3D) right).bottomMinVertex  = Vector3D.copyVector3D(((Curve3D) start).bottomMinVertex);
        	((Curve3D) right).frontMinVertex  = Vector3D.copyVector3D(((Curve3D) start).frontMinVertex);
        	((Curve3D) right).backMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).backMaxVertex);
        	((Curve3D) right).leftMinVertex  = Vector3D.copyVector3D(((Curve3D) start).leftMinVertex);
        	((Curve3D) right).rightMaxVertex  = Vector3D.copyVector3D(((Curve3D) start).rightMaxVertex);
        	
        	((Curve3D) right).insideVertex  = Vector3D.copyVector3D(((Curve3D) start).insideVertex);
        	
        	insideVertex = Vector3D.copyVector3D(((Curve3D) start).insideVertex);
        	
        	
        	
        }
        
        ObjectGroup3D leftGroup, rightGroup;
        
        int insideVertexPos =  plane.planePosition(insideVertex);
  
        for (int i = 0; i < owp.numFacets; i++)
        {   
            if (!owp.hasReplacement(owp.facets[i]))
            {   // planepos gebruiken
                // om te kijken waar facet heen moet
                int leftPos = 0;
                int onPos = 0;
                int rightPos = 0;
                for (int j = 0; j < owp.facets[i].numPoints; j++)
                {   int pPos = plane.planePosition(owp.facets[i].points[j]);
                    if (pPos == -1)
                        leftPos++;
                    else if (pPos == 1)
                        rightPos++;    
                    else // pPos == 0
                        onPos++;
// cut apart bekijken, kom je vanzelf tegen
// de cut hoort rechts(!)
// zijn omgekeerde links
            
                } // points of facet[i]
                // facet is positioned left of cut
                if ((leftPos > 0) && (onPos >= 0) && (insideVertexPos == -1))
                {   // add facet to left
                    int firstIndex = left.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        left.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D leftFacet = new Facet3D(left.vertices, inds, owp.facets[i].color);
                    left.addFacet(leftFacet);
                    if (owp.facets[i].numPoints == leftFacet.numPoints)
                    	Facet3D.copyAttributes(owp.facets[i], leftFacet, true);
                    else
                    	Facet3D.copyAttributes(owp.facets[i], leftFacet, false);
                    
                    int inPlaneEdgeIndex = -1;
                    for (int vCnt = 0; vCnt < leftFacet.numPoints; vCnt++)
                    {	int pPos1 = plane.planePosition(leftFacet.points[vCnt]);
                    	int pPos2 = plane.planePosition(leftFacet.points[(vCnt + 1) % leftFacet.numPoints]);
                    	boolean inPlane = (pPos1 == 0) && (pPos2 == 0);
                    	if (inPlane)
                    		inPlaneEdgeIndex = vCnt;
                    }
                    if (inPlaneEdgeIndex >= 0)
                    {	leftFacet.edgeCodes[inPlaneEdgeIndex] = 52;
//System.out.println("left 52");                    
                    }
                    // update cut colors?
                    
                }    
                // facet is positioned right of cut
                else if ((rightPos > 0) && (onPos >= 0) && (insideVertexPos == 1))
                {   // add facet to right
                    int firstIndex = right.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        right.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D rightFacet = new Facet3D(right.vertices, inds, owp.facets[i].color);
                    right.addFacet(rightFacet);
                    if (owp.facets[i].numPoints == rightFacet.numPoints)
                    	Facet3D.copyAttributes(owp.facets[i], rightFacet, true);
                    else
                    	Facet3D.copyAttributes(owp.facets[i], rightFacet, false);
                    
                    int inPlaneEdgeIndex = -1;
                    for (int vCnt = 0; vCnt < rightFacet.numPoints; vCnt++)
                    {	int pPos1 = plane.planePosition(rightFacet.points[vCnt]);
                    	int pPos2 = plane.planePosition(rightFacet.points[(vCnt + 1) % rightFacet.numPoints]);
                    	boolean inPlane = (pPos1 == 0) && (pPos2 == 0);
                    	if (inPlane)
                    		inPlaneEdgeIndex = vCnt;
                    }
                    if (inPlaneEdgeIndex >= 0)
                    	rightFacet.edgeCodes[inPlaneEdgeIndex] = 52;
                    // update cut colors?
                    
                }    
                
// dit gebeurt niet wanneer je geen cut maakt
                
                else if ((leftPos == 0) && (rightPos == 0))
                {   
                	
System.out.println("(leftPos == 0) && (rightPos == 0)");                	
                	// facet is the cut, add to right
                    int firstIndex = right.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        right.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D rightCutFacet = new Facet3D(right.vertices, inds, graphColor);
                    right.addFacet(rightCutFacet);
                    Facet3D.copyAttributes(owp.facets[i], rightCutFacet, false);
                    // update cut colors and not outlined
                    rightCutFacet.color = graphColor;                    
                    // note: there is only one cut!
                    for (int m = 0; m < rightCutFacet.numPoints; m++)
                        rightCutFacet.edgeCodes[m] = 0;
                    
                    // add reverse facet to left
                    firstIndex = left.numVertices;
                    for (int j = owp.facets[i].numPoints - 1; j >= 0; j--)
                        left.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D leftCutFacet = new Facet3D(left.vertices, inds, graphColor);
                    left.addFacet(leftCutFacet);
                    Facet3D.copyAttributes(owp.facets[i], leftCutFacet, false);
                    leftCutFacet.color = graphColor;
                    for (int m = 0; m < leftCutFacet.numPoints; m++)
                        leftCutFacet.edgeCodes[m] = 0;
                    
                    
                    
                } // allocation of facet[i]   
            
            } // !hasReplacement facet[i]
            
        } // owp facet loop    

//System.out.println("left " + leftVerticesLabeled.size());            
//for (int lft = 0; lft < leftVertexLabels.size(); lft++)
//System.out.println((String) leftVertexLabels.elementAt(lft));
//System.out.println("right " + rightVerticesLabeled.size());                        
        // find true center and diameter    

        if (left.numVertices > right.numVertices)
        {   left.initObject3D(true, false);
        	right = null;
        	return left;
        }
        else
        {   right.initObject3D(true, false);
        	left = null;
        	return right;
        }
        

// note: up to here the labelling of the two basic halves is consistent
// with that of the original basic object

// now find all OTHER labels present in the original object

/*
new omitted         
        Vector otherVerticesLabeled = new Vector();
        Vector otherVertexLabels = new Vector();
        // assume ob's facetArray is fixed
        for (int obFCnt = 0; obFCnt < ob.numFacets; obFCnt++)
        {   for (int obVCnt = 0; obVCnt < ob.facets[obFCnt].numPoints; obVCnt++)
            {   Vector3D oVertex = ob.facets[obFCnt].points[obVCnt];
                String oLabel = ob.facets[obFCnt].vertexLabels[obVCnt];
                if ((oLabel != null) && 
                    !oLabel.equals("") && !oLabel.equals("XX")
                   ) 
                {    if (!otherVerticesLabeled.contains(oVertex))
                     {    otherVerticesLabeled.addElement(oVertex);
                          otherVertexLabels.addElement(oLabel); 
                         
                     }  
                }   
            }
        }
*/        
/*        
new omitted 
        // find maximum labelindex of ob
        int otherIndex = 0;
        for (int oCnt = 0; oCnt < otherVertexLabels.size(); oCnt++)
        {   otherIndex = Math.max(otherIndex,
                getLabelIndex((String) otherVertexLabels.elementAt(oCnt)));
        }    
*/        

//System.out.println("left-vert = " + left.numVertices);
//System.out.println("right-vert = " + right.numVertices);

        
/*                
        Vector3D trVector = new Vector3D(plane.normal);
        Vector3D.scaleBy(trVector, ob.diameter / 3);
               
        Vector3D minTrVector = Vector3D.minus(new Vector3D(0,0,0), trVector);

        double trPos = Vector3D.dotProduct(plane.normal, trVector) -
                       Vector3D.dotProduct(plane.normal, plane.point);
        double minTrPos = Vector3D.dotProduct(plane.normal, minTrVector) -
                                  Vector3D.dotProduct(plane.normal, plane.point);

        if (trPos < minTrPos)
        {   //left.translateBy(trVector.x, trVector.y, trVector.z);
            //right.translateBy(minTrVector.x, minTrVector.y, minTrVector.z);
        }
        else
        {   //left.translateBy(minTrVector.x, minTrVector.y, minTrVector.z);
            //right.translateBy(trVector.x, trVector.y, trVector.z);
                
        }
*/        

/*        
        Vector origConstruction = new Vector();   
        if (ob instanceof ObjectWithPlane)
            origConstruction = ((ObjectWithPlane) ob).getConstruction();
        else if (ob instanceof ObjectWithLine)
            origConstruction = ((ObjectWithLine) ob).getConstruction();
        origConstruction.removeElement(plane);
  
        if (left != null)
        {	leftGroup = ObjectWithPlane.rebuild(left, origConstruction);
        	leftGroup.fixFacetArray();
        	return leftGroup;
        }
        else
        {   rightGroup = ObjectWithPlane.rebuild(left, origConstruction);
        	rightGroup.fixFacetArray();
        	return rightGroup;
        }
*/        
        
/*        
        Vector trConstruction = new Vector();
        Vector minTrConstruction = new Vector();
        for (int i = 0; i < origConstruction.size(); i++)
        {   Object conObject = origConstruction.elementAt(i);
            if (conObject instanceof Line3D)
            {   Line3D trLine = ((Line3D) conObject).translateBy(trVector);
                trConstruction.addElement(trLine);
                Line3D minTrLine = ((Line3D) conObject).translateBy(minTrVector);
                minTrConstruction.addElement(minTrLine);            
            
            }
            else if (conObject instanceof Plane3D)
            {   Plane3D trPlane = ((Plane3D) conObject).translateBy(trVector);
                trConstruction.addElement(trPlane);
                Plane3D minTrPlane = ((Plane3D) conObject).translateBy(minTrVector);
                minTrConstruction.addElement(minTrPlane);            
                    
            }    
        }
*/                
//System.out.println("" + trConstruction.size());            
//System.out.println("" + minTrConstruction.size());            
  
/*        
        if (trPos < minTrPos)
        {   leftGroup = ObjectWithPlane.rebuild(left, trConstruction);
            leftGroup.fixFacetArray();
//            int labelCnt = otherIndex;
            for (int lFCnt = 0; lFCnt < leftGroup.numFacets; lFCnt ++)
            {   for (int lVCnt = 0; lVCnt < leftGroup.facets[lFCnt].numPoints; lVCnt ++)
                {   // study this vertex
                    Vector3D lVertex = leftGroup.facets[lFCnt].points[lVCnt];
                    String lLabel = leftGroup.facets[lFCnt].vertexLabels[lVCnt];
                    Vector3D trLVertex = new Vector3D(lVertex);
                    // translate back
                    Vector3D.translateBy(trLVertex, -trVector.x, -trVector.y, -trVector.z);
                    
//new omitted
                    // if lVertex has a Label
                    if ((lLabel != null) && !lLabel.equals("") && !lLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trLVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trLVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        {   labelCnt++;
                            lLabel = getLabel(labelCnt);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(lLabel);
                            otherVerticesLabeled.addElement(trLVertex);
                            otherVertexLabels.addElement(new String (lLabel));
                        }    
                        
                    }
// einde new omitted                    
                }    
            }
            
            rightGroup = ObjectWithPlane.rebuild(right, minTrConstruction);
            rightGroup.fixFacetArray();            
            
            for (int rFCnt = 0; rFCnt < rightGroup.numFacets; rFCnt ++)
            {   for (int rVCnt = 0; rVCnt < rightGroup.facets[rFCnt].numPoints; rVCnt ++)
                {   // study this vertex
                    Vector3D rVertex = rightGroup.facets[rFCnt].points[rVCnt];
                    String rLabel = rightGroup.facets[rFCnt].vertexLabels[rVCnt];
                    Vector3D trRVertex = new Vector3D(rVertex);
                    // translate back
                    Vector3D.translateBy(trRVertex, -minTrVector.x, -minTrVector.y, -minTrVector.z);
         
//new omitted            
                    // if rVertex has a Label
                    if ((rLabel != null) && !rLabel.equals("") && !rLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trRVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trRVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        // cannot happen?
                        {   labelCnt++;
                            rLabel = getLabel(labelCnt);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(rLabel);
                            otherVerticesLabeled.addElement(trRVertex);
                            otherVertexLabels.addElement(new String (rLabel));
                        }    
                        
                    }
//einde new omitted                    
                }    
            }
            
        }
*/        
/*        
        else // trPos >= minTrPos 
        {   

            leftGroup = ObjectWithPlane.rebuild(left, minTrConstruction);
            leftGroup.fixFacetArray();            
//            int labelCnt = otherIndex;            
            for (int lFCnt = 0; lFCnt < leftGroup.numFacets; lFCnt ++)
            {   for (int lVCnt = 0; lVCnt < leftGroup.facets[lFCnt].numPoints; lVCnt ++)
                {   // study this vertex
                    Vector3D lVertex = leftGroup.facets[lFCnt].points[lVCnt];
                    String lLabel = leftGroup.facets[lFCnt].vertexLabels[lVCnt];
                    Vector3D trLVertex = new Vector3D(lVertex);
                    Vector3D.translateBy(trLVertex, -minTrVector.x, -minTrVector.y, -minTrVector.z);
                    
//new omitted
                    // if lVertex has a Label
                    if ((lLabel != null) && !lLabel.equals("") && !lLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trLVertex))
                        {   // relabel                        
                            int index = otherVerticesLabeled.indexOf(trLVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        {   labelCnt++;
                            lLabel = getLabel(labelCnt);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(lLabel);
                            otherVerticesLabeled.addElement(trLVertex);
                            otherVertexLabels.addElement(new String (lLabel));
                        }    
                        
                    }
// einde new omitted                    
                }    
            }
            
            rightGroup = ObjectWithPlane.rebuild(right, trConstruction);
            rightGroup.fixFacetArray();            

            for (int rFCnt = 0; rFCnt < rightGroup.numFacets; rFCnt ++)
            {   for (int rVCnt = 0; rVCnt < rightGroup.facets[rFCnt].numPoints; rVCnt ++)
                {   // study this vertex
                    Vector3D rVertex = rightGroup.facets[rFCnt].points[rVCnt];
                    String rLabel = rightGroup.facets[rFCnt].vertexLabels[rVCnt];
                    Vector3D trRVertex = new Vector3D(rVertex);
                    // translate back
                    Vector3D.translateBy(trRVertex, -trVector.x, -trVector.y, -trVector.z);
                    
//new omitted
                    // if rVertex has a Label
                    if ((rLabel != null) && !rLabel.equals("") && !rLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trRVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trRVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        // cannot happen?
                        {   labelCnt++;
                            rLabel = getLabel(labelCnt);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(rLabel);
                            otherVerticesLabeled.addElement(trRVertex);
                            otherVertexLabels.addElement(new String (rLabel));
                        }    
                        
                    }
//einde new omitted            
                }    
            }
                
        }
*/

        // leftGroup, rightGroup have correct diameter and translated center

/*        
        // rebuild de twee stukken
        ObjectGroup3D result = new ObjectGroup3D();
        result.addObject3D(left);
        result.addObject3D(right);
        // force center and diameter
        result.initObject3D(true, new Vector3D(ob.center), ob.diameter, false);
        return result;
*/        
        
        
    } //    

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MouseHandler

	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - grafiek3DCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiek3DCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - grafiek3DCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiek3DCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		//if (!draaibaar)
		//	return;

      	xClicked = eventX;
        yClicked = eventY;

//        if ((mouseMode == INERT) || (mouseMode == FOLDOUT) || (mouseMode == CUTOBJECT))
//        {   
        	panel3D.oldX = eventX;
            panel3D.oldY = eventY;
            xStart = eventX;
            yStart = eventY;
            dragging = true;
              

//      } // other button(s)

	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if (dragging)
        {
            if ((eventX <= 0) || (eventY <= 0) ||
                (eventX >= panel3D.breedte) || (eventY >= panel3D.hoogte) 
                )
            {    
                dragging = false;
                return;    
            }

//dit doet WireFrame met 360 i.p.v. 180 graden
            double xTheta = (panel3D.oldY - eventY) * 180.0d /
                             panel3D.breedte;
            double yTheta = (panel3D.oldX - eventX) * 180.0d /
                             panel3D.hoogte;

            panel3D.rotateCake(xTheta, yTheta);                
            
            panel3D.repaint();
            
            panel3D.oldX = eventX;
            panel3D.oldY = eventY;

        }
        else
        {   
            dragging = false;
        }    

	}
	
	public void mouseUpTouchEndAction()
	{
		if (dragging)
      {
          // make sure the dragg-event-queue for rotating is completed!!
          panel3D.repaint();
          dragging = false;
      }
	}

    
	public void zetOpdracht(HashMap<String,Object> b, String[] randomVars, HashMap<String,Object> randomValues)
	{
		
System.out.println("g3dc zetOpdracht");

		// edit state
		boolean zoomOptie = true;
		boolean translateOptie = true;
		boolean solidDraadKeuzeOptie = true;
		boolean finerKeuzeOptie = true;
		boolean asKeuzeOptie = true;
		boolean labelKeuzeOptie = true;
		boolean projectieKeuzeOptie = true;
		boolean kleurKeuzeOptie = true;

		// functieTypeKeuze in functieEditor
		boolean figuurIsDemo = false;
		
		if (b.containsKey("zoomOptie"))
			zoomOptie = ((Boolean) b.get("zoomOptie")).booleanValue();
		if (b.containsKey("translateOptie"))
			translateOptie = ((Boolean) b.get("translateOptie")).booleanValue();
		if (b.containsKey("solidDraadKeuzeOptie"))
			solidDraadKeuzeOptie = ((Boolean) b.get("solidDraadKeuzeOptie")).booleanValue();
		if (b.containsKey("finerKeuzeOptie"))
			finerKeuzeOptie = ((Boolean) b.get("finerKeuzeOptie")).booleanValue();
		if (b.containsKey("asKeuzeOptie"))
			asKeuzeOptie = ((Boolean) b.get("asKeuzeOptie")).booleanValue();
		if (b.containsKey("labelKeuzeOptie"))
			labelKeuzeOptie = ((Boolean) b.get("labelKeuzeOptie")).booleanValue();
		if (b.containsKey("projectieKeuzeOptie"))
			projectieKeuzeOptie = ((Boolean) b.get("projectieKeuzeOptie")).booleanValue();
		if (b.containsKey("kleurKeuzeOptie"))
			kleurKeuzeOptie = ((Boolean) b.get("kleurKeuzeOptie")).booleanValue();

		if (b.containsKey("figuurIsDemo"))
			figuurIsDemo = ((Boolean) b.get("figuurIsDemo")).booleanValue();

		zetZoomOptie(zoomOptie);
		zetTranslateOptie(translateOptie);
		zetSolidDraadKeuzeOptie(solidDraadKeuzeOptie);
		zetFinerKeuzeOptie(finerKeuzeOptie);
		zetAsKeuzeOptie(asKeuzeOptie);
		zetLabelKeuzeOptie(labelKeuzeOptie);
		zetProjectieKeuzeOptie(projectieKeuzeOptie);
		zetKleurKeuzeOptie(kleurKeuzeOptie);
		
		this.figuurIsDemo = figuurIsDemo;		
		//zetFiguurIsDemo(figuurIsDemo);
		
		// state
		int objectType = FUNCTION;
		
		double angleXG = Object3DContainer.angleXStart;
		double angleZG = Object3DContainer.angleZStart;
		double angleXS = Object3DContainer.angleXStart;
		double angleZS = Object3DContainer.angleZStart;
		double angleXC = Object3DContainer.angleXStart;
		double angleZC = Object3DContainer.angleZStart;
		
		int zoomFactorG = 0;
		int translateXFactorG = 0;
		int translateYFactorG = 0;
		int translateZFactorG = 0;
		
		int zoomFactorS = 0;
		int translateXFactorS = 0;
		int translateYFactorS = 0;
		int translateZFactorS = 0;

		int zoomFactorC = 0;
		int translateXFactorC = 0;
		int translateYFactorC = 0;
		int translateZFactorC = 0;
		
		boolean wireFrameG = false;
		boolean wireFrameS = false;
		
		int finerFactorG = 0;
		
		boolean noAxesG = false;
		boolean noAxesS = false;
		boolean noAxesC = false;
		
		int floorTypeG = NOFLOOR;
		int floorTypeS = NOFLOOR;
		int floorTypeC = NOFLOOR;
		
	    int labelTypeG = ENDLABELS;
	    int labelTypeS = ENDLABELS;
	    int labelTypeC = ENDLABELS;
	    
	    boolean centraleProjG = true;
	    boolean centraleProjS = true;
	    boolean centraleProjC = true;
	    
	    CssColor graphColor = transYellow;
	    CssColor surfaceColor = transYellow;
		
		if (b.containsKey("objectType"))
			objectType = ((Integer) b.get("objectType")).intValue();
		this.objectType = objectType;
		
		// FUNCTION
		if (b.containsKey("angleXG"))
			angleXG = ((Double) b.get("angleXG")).doubleValue();
		if (b.containsKey("angleZG"))
			angleZG = ((Double) b.get("angleZG")).doubleValue();
		this.angleXG = angleXG;
		this.angleZG = angleZG;
		
		if (b.containsKey("zoomFactorG"))
			zoomFactorG = ((Integer) b.get("zoomFactorG")).intValue();
		if (zoomFactorG < 0)
		{	for (int zUitCnt = zoomFactorG; zUitCnt < 0; zUitCnt++)
				zoomUit(false, FUNCTION);
		}
		if (zoomFactorG > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorG; zInCnt++)
				zoomIn(false, FUNCTION);
		}
		
		if (b.containsKey("translateXFactorG"))
			translateXFactorG = ((Integer) b.get("translateXFactorG")).intValue();
		if (translateXFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorG; tPlusCnt++)
				transPlusX(false, FUNCTION);
		}
		if (translateXFactorG < 0)
		{	for (int tMinCnt = translateXFactorG; tMinCnt < 0; tMinCnt++)
				transMinX(false, FUNCTION);
		}
		if (b.containsKey("translateYFactorG"))
			translateYFactorG = ((Integer) b.get("translateYFactorG")).intValue();
		if (translateYFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorG; tPlusCnt++)
				transPlusY(false, FUNCTION);
		}
		if (translateYFactorG < 0)
		{	for (int tMinCnt = translateYFactorG; tMinCnt < 0; tMinCnt++)
				transMinY(false, FUNCTION);
		}
		if (b.containsKey("translateZFactorG"))
			translateZFactorG = ((Integer) b.get("translateZFactorG")).intValue();
		if (translateZFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorG; tPlusCnt++)
				transPlusZ(false, FUNCTION);
		}
		if (translateZFactorG < 0)
		{	for (int tMinCnt = translateZFactorG; tMinCnt < 0; tMinCnt++)
				transMinZ(false, FUNCTION);
		}
		
		if (b.containsKey("wireFrameG"))
			wireFrameG = ((Boolean) b.get("wireFrameG")).booleanValue();
		this.wireFrameG = wireFrameG;

		if (b.containsKey("finerFactorG"))
			finerFactorG = ((Integer) b.get("finerFactorG")).intValue();
		if (finerFactorG > 0)
		{	for (int fPlusCnt = 0; fPlusCnt < finerFactorG; fPlusCnt++)
				zetFijner(false, FUNCTION);
		}
		if (finerFactorG < 0)
		{	for (int fMinCnt = finerFactorG; fMinCnt < 0; fMinCnt++)
				zetGrover(false, FUNCTION);
		}
		
		if (b.containsKey("noAxesG"))
			noAxesG = ((Boolean) b.get("noAxesG")).booleanValue();
		if (b.containsKey("floorTypeG"))
			floorTypeG = ((Integer) b.get("floorTypeG")).intValue();
		this.noAxesG = noAxesG;
		this.floorTypeG = floorTypeG;

		if (b.containsKey("labelTypeG"))
			labelTypeG = ((Integer) b.get("labelTypeG")).intValue();
		this.labelTypeG = labelTypeG;
		
		if (b.containsKey("centraleProjG"))
			centraleProjG = ((Boolean) b.get("centraleProjG")).booleanValue();
		this.centraleProjG = centraleProjG;
		
		if (b.containsKey("graphColor"))
			graphColor = (CssColor) b.get("graphColor");
		this.graphColor = graphColor;
		
		// SURFACE
		if (b.containsKey("angleXS"))
			angleXS = ((Double) b.get("angleXS")).doubleValue();
		if (b.containsKey("angleZS"))
			angleZS = ((Double) b.get("angleZS")).doubleValue();
		this.angleXS = angleXS;
		this.angleZS = angleZS;
		
		if (b.containsKey("zoomFactorS"))
			zoomFactorS = ((Integer) b.get("zoomFactorS")).intValue();
		
//System.out.println("zFS = " + zoomFactorS);

		if (zoomFactorS < 0)
		{	for (int zUitCnt = zoomFactorS; zUitCnt < 0; zUitCnt++)
				zoomUit(false, SURFACE);
		}
		if (zoomFactorS > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorS; zInCnt++)
				zoomIn(false, SURFACE);
		}
		
		if (b.containsKey("translateXFactorS"))
			translateXFactorS = ((Integer) b.get("translateXFactorS")).intValue();
		if (translateXFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorS; tPlusCnt++)
				transPlusX(false, SURFACE);
		}
		if (translateXFactorS < 0)
		{	for (int tMinCnt = translateXFactorS; tMinCnt < 0; tMinCnt++)
				transMinX(false, SURFACE);
		}
		if (b.containsKey("translateYFactorS"))
			translateYFactorS = ((Integer) b.get("translateYFactorS")).intValue();
		if (translateYFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorS; tPlusCnt++)
				transPlusY(false, SURFACE);
		}
		if (translateYFactorS < 0)
		{	for (int tMinCnt = translateYFactorS; tMinCnt < 0; tMinCnt++)
				transMinY(false, SURFACE);
		}
		if (b.containsKey("translateZFactorS"))
			translateZFactorS = ((Integer) b.get("translateZFactorS")).intValue();
		if (translateZFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorS; tPlusCnt++)
				transPlusZ(false, SURFACE);
		}
		if (translateZFactorS < 0)
		{	for (int tMinCnt = translateZFactorS; tMinCnt < 0; tMinCnt++)
				transMinZ(false, SURFACE);
		}
		
		if (b.containsKey("wireFrameS"))
			wireFrameS = ((Boolean) b.get("wireFrameS")).booleanValue();
		this.wireFrameS = wireFrameS;

		if (b.containsKey("noAxesS"))
			noAxesS = ((Boolean) b.get("noAxesS")).booleanValue();
		if (b.containsKey("floorTypeS"))
			floorTypeS = ((Integer) b.get("floorTypeS")).intValue();
		this.noAxesS = noAxesS;
		this.floorTypeS = floorTypeS;

		if (b.containsKey("labelTypeS"))
			labelTypeS = ((Integer) b.get("labelTypeS")).intValue();
		this.labelTypeS = labelTypeS;
		
		if (b.containsKey("centraleProjS"))
			centraleProjS = ((Boolean) b.get("centraleProjS")).booleanValue();
		this.centraleProjS = centraleProjS;
		
		if (b.containsKey("surfaceColor"))
			surfaceColor = (CssColor) b.get("surfaceColor");
		this.surfaceColor = surfaceColor;
		
		// CURVE
		if (b.containsKey("angleXC"))
			angleXC = ((Double) b.get("angleXC")).doubleValue();
		if (b.containsKey("angleZC"))
			angleZC = ((Double) b.get("angleZC")).doubleValue();
		this.angleXC = angleXC;
		this.angleZC = angleZC;
		
		if (b.containsKey("zoomFactorC"))
			zoomFactorC = ((Integer) b.get("zoomFactorC")).intValue();
		if (zoomFactorC < 0)
		{	for (int zUitCnt = zoomFactorC; zUitCnt < 0; zUitCnt++)
				zoomUit(false, CURVE);
		}
		if (zoomFactorC > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorC; zInCnt++)
			zoomIn(false, CURVE);
		}
		
		if (b.containsKey("translateXFactorC"))
			translateXFactorC = ((Integer) b.get("translateXFactorC")).intValue();
		if (translateXFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorC; tPlusCnt++)
				transPlusX(false, CURVE);
		}
		if (translateXFactorC < 0)
		{	for (int tMinCnt = translateXFactorC; tMinCnt < 0; tMinCnt++)
				transMinX(false, CURVE);
		}
		if (b.containsKey("translateYFactorC"))
			translateYFactorC = ((Integer) b.get("translateYFactorC")).intValue();
		if (translateYFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorC; tPlusCnt++)
				transPlusY(false, CURVE);
		}
		if (translateYFactorC < 0)
		{	for (int tMinCnt = translateYFactorC; tMinCnt < 0; tMinCnt++)
				transMinY(false, CURVE);
		}
		if (b.containsKey("translateZFactorC"))
			translateZFactorC = ((Integer) b.get("translateZFactorC")).intValue();
		if (translateZFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorC; tPlusCnt++)
				transPlusZ(false, CURVE);
		}
		if (translateZFactorC < 0)
		{	for (int tMinCnt = translateZFactorC; tMinCnt < 0; tMinCnt++)
				transMinZ(false, CURVE);
		}

		if (b.containsKey("noAxesC"))
			noAxesC = ((Boolean) b.get("noAxesC")).booleanValue();
		if (b.containsKey("floorTypeC"))
			floorTypeC = ((Integer) b.get("floorTypeC")).intValue();
		this.noAxesC = noAxesC;
		this.floorTypeC = floorTypeC;
		
		if (b.containsKey("labelTypeC"))
			labelTypeC = ((Integer) b.get("labelTypeC")).intValue();
		this.labelTypeC = labelTypeC;

		if (b.containsKey("centraleProjC"))
			centraleProjC = ((Boolean) b.get("centraleProjC")).booleanValue();
		this.centraleProjC = centraleProjC;
		
		// hier, objectType nodig
		//layoutKnoppenPanel();
		
		//functieEditor.zetOpdracht(b, randomVars, randomValues);
		
		// hier !!
// grafiek oppervlak kromme maken
// model maken		
// zetBeginHoeken		

	}
	
	public void setState(HashMap<String,Object> b)
	{
		
System.out.println("g3dc setState");		
		
		// state
		int objectType = FUNCTION;
		
		double angleXG = Object3DContainer.angleXStart;
		double angleZG = Object3DContainer.angleZStart;
		double angleXS = Object3DContainer.angleXStart;
		double angleZS = Object3DContainer.angleZStart;
		double angleXC = Object3DContainer.angleXStart;
		double angleZC = Object3DContainer.angleZStart;
		
		int zoomFactorG = 0;
		int translateXFactorG = 0;
		int translateYFactorG = 0;
		int translateZFactorG = 0;
		
		int zoomFactorS = 0;
		int translateXFactorS = 0;
		int translateYFactorS = 0;
		int translateZFactorS = 0;

		int zoomFactorC = 0;
		int translateXFactorC = 0;
		int translateYFactorC = 0;
		int translateZFactorC = 0;
		
		boolean wireFrameG = false;
		boolean wireFrameS = false;
		
		int finerFactorG = 0;
		
		boolean noAxesG = false;
		boolean noAxesS = false;
		boolean noAxesC = false;
		
		int floorTypeG = NOFLOOR;
		int floorTypeS = NOFLOOR;
		int floorTypeC = NOFLOOR;
		
	    int labelTypeG = ENDLABELS;
	    int labelTypeS = ENDLABELS;
	    int labelTypeC = ENDLABELS;
	    
	    boolean centraleProjG = true;
	    boolean centraleProjS = true;
	    boolean centraleProjC = true;
	    
	    CssColor graphColor = transYellow;
	    CssColor surfaceColor = transYellow;
		
		if (b.containsKey("objectType"))
			objectType = ((Integer) b.get("objectType")).intValue();
		this.objectType = objectType;

		zoomStandaard(false, objectType);
		
		// FUNCTION
		if (b.containsKey("angleXG"))
			angleXG = ((Double) b.get("angleXG")).doubleValue();
		if (b.containsKey("angleZG"))
			angleZG = ((Double) b.get("angleZG")).doubleValue();
		this.angleXG = angleXG;
		this.angleZG = angleZG;
		
		if (b.containsKey("zoomFactorG"))
			zoomFactorG = ((Integer) b.get("zoomFactorG")).intValue();
		if (zoomFactorG < 0)
		{	for (int zUitCnt = zoomFactorG; zUitCnt < 0; zUitCnt++)
				zoomUit(false, FUNCTION);
		}
		if (zoomFactorG > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorG; zInCnt++)
				zoomIn(false, FUNCTION);
		}
		
		if (b.containsKey("translateXFactorG"))
			translateXFactorG = ((Integer) b.get("translateXFactorG")).intValue();
		if (translateXFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorG; tPlusCnt++)
				transPlusX(false, FUNCTION);
		}
		if (translateXFactorG < 0)
		{	for (int tMinCnt = translateXFactorG; tMinCnt < 0; tMinCnt++)
				transMinX(false, FUNCTION);
		}
		if (b.containsKey("translateYFactorG"))
			translateYFactorG = ((Integer) b.get("translateYFactorG")).intValue();
		if (translateYFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorG; tPlusCnt++)
				transPlusY(false, FUNCTION);
		}
		if (translateYFactorG < 0)
		{	for (int tMinCnt = translateYFactorG; tMinCnt < 0; tMinCnt++)
				transMinY(false, FUNCTION);
		}
		if (b.containsKey("translateZFactorG"))
			translateZFactorG = ((Integer) b.get("translateZFactorG")).intValue();
		if (translateZFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorG; tPlusCnt++)
				transPlusZ(false, FUNCTION);
		}
		if (translateZFactorG < 0)
		{	for (int tMinCnt = translateZFactorG; tMinCnt < 0; tMinCnt++)
				transMinZ(false, FUNCTION);
		}
		
		if (b.containsKey("wireFrameG"))
			wireFrameG = ((Boolean) b.get("wireFrameG")).booleanValue();
		this.wireFrameG = wireFrameG;

		if (b.containsKey("finerFactorG"))
			finerFactorG = ((Integer) b.get("finerFactorG")).intValue();
		if (finerFactorG > 0)
		{	for (int fPlusCnt = 0; fPlusCnt < finerFactorG; fPlusCnt++)
				zetFijner(false, FUNCTION);
		}
		if (finerFactorG < 0)
		{	for (int fMinCnt = finerFactorG; fMinCnt < 0; fMinCnt++)
				zetGrover(false, FUNCTION);
		}
		
		if (b.containsKey("noAxesG"))
			noAxesG = ((Boolean) b.get("noAxesG")).booleanValue();
		if (b.containsKey("floorTypeG"))
			floorTypeG = ((Integer) b.get("floorTypeG")).intValue();
		this.noAxesG = noAxesG;
		this.floorTypeG = floorTypeG;

		if (b.containsKey("labelTypeG"))
			labelTypeG = ((Integer) b.get("labelTypeG")).intValue();
		this.labelTypeG = labelTypeG;
		
		if (b.containsKey("centraleProjG"))
			centraleProjG = ((Boolean) b.get("centraleProjG")).booleanValue();
		this.centraleProjG = centraleProjG;
		
		if (b.containsKey("graphColor"))
			graphColor = (CssColor) b.get("graphColor");
		this.graphColor = graphColor;
		
		
		// SURFACE
		if (b.containsKey("angleXS"))
			angleXS = ((Double) b.get("angleXS")).doubleValue();
		if (b.containsKey("angleZS"))
			angleZS = ((Double) b.get("angleZS")).doubleValue();
		this.angleXS = angleXS;
		this.angleZS = angleZS;
		
		if (b.containsKey("zoomFactorS"))
			zoomFactorS = ((Integer) b.get("zoomFactorS")).intValue();
		if (zoomFactorS < 0)
		{	for (int zUitCnt = zoomFactorS; zUitCnt < 0; zUitCnt++)
			zoomUit(false, SURFACE);
		}
		if (zoomFactorS > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorS; zInCnt++)
			zoomIn(false, SURFACE);
		}
		
		if (b.containsKey("translateXFactorS"))
			translateXFactorS = ((Integer) b.get("translateXFactorS")).intValue();
		if (translateXFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorS; tPlusCnt++)
				transPlusX(false, SURFACE);
		}
		if (translateXFactorS < 0)
		{	for (int tMinCnt = translateXFactorS; tMinCnt < 0; tMinCnt++)
				transMinX(false, SURFACE);
		}
		if (b.containsKey("translateYFactorS"))
			translateYFactorS = ((Integer) b.get("translateYFactorS")).intValue();
		if (translateYFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorS; tPlusCnt++)
				transPlusY(false, SURFACE);
		}
		if (translateYFactorS < 0)
		{	for (int tMinCnt = translateYFactorS; tMinCnt < 0; tMinCnt++)
				transMinY(false, SURFACE);
		}
		if (b.containsKey("translateZFactorS"))
			translateZFactorS = ((Integer) b.get("translateZFactorS")).intValue();
		if (translateZFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorS; tPlusCnt++)
				transPlusZ(false, SURFACE);
		}
		if (translateZFactorS < 0)
		{	for (int tMinCnt = translateZFactorS; tMinCnt < 0; tMinCnt++)
				transMinZ(false, SURFACE);
		}
		
		if (b.containsKey("wireFrameS"))
			wireFrameS = ((Boolean) b.get("wireFrameS")).booleanValue();
		this.wireFrameS = wireFrameS;

		if (b.containsKey("noAxesS"))
			noAxesS = ((Boolean) b.get("noAxesS")).booleanValue();
		if (b.containsKey("floorTypeS"))
			floorTypeS = ((Integer) b.get("floorTypeS")).intValue();
		this.noAxesS = noAxesS;
		this.floorTypeS = floorTypeS;

		if (b.containsKey("labelTypeS"))
			labelTypeS = ((Integer) b.get("labelTypeS")).intValue();
		this.labelTypeS = labelTypeS;

		if (b.containsKey("centraleProjS"))
			centraleProjS = ((Boolean) b.get("centraleProjS")).booleanValue();
		this.centraleProjS = centraleProjS;
		
		if (b.containsKey("surfaceColor"))
			surfaceColor = (CssColor) b.get("surfaceColor");
		this.surfaceColor = surfaceColor;
		
		// CURVE
		if (b.containsKey("angleXC"))
			angleXC = ((Double) b.get("angleXC")).doubleValue();
		if (b.containsKey("angleZC"))
			angleZC = ((Double) b.get("angleZC")).doubleValue();
		this.angleXC = angleXC;
		this.angleZC = angleZC;
		
		if (b.containsKey("zoomFactorC"))
			zoomFactorC = ((Integer) b.get("zoomFactorC")).intValue();
		if (zoomFactorC < 0)
		{	for (int zUitCnt = zoomFactorC; zUitCnt < 0; zUitCnt++)
				zoomUit(false, CURVE);
		}
		if (zoomFactorC > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorC; zInCnt++)
			zoomIn(false, CURVE);
		}
		
		if (b.containsKey("translateXFactorC"))
			translateXFactorC = ((Integer) b.get("translateXFactorC")).intValue();
		if (translateXFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorC; tPlusCnt++)
				transPlusX(false, CURVE);
		}
		if (translateXFactorC < 0)
		{	for (int tMinCnt = translateXFactorC; tMinCnt < 0; tMinCnt++)
				transMinX(false, CURVE);
		}
		if (b.containsKey("translateYFactorC"))
			translateYFactorC = ((Integer) b.get("translateYFactorC")).intValue();
		if (translateYFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorC; tPlusCnt++)
				transPlusY(false, CURVE);
		}
		if (translateYFactorC < 0)
		{	for (int tMinCnt = translateYFactorC; tMinCnt < 0; tMinCnt++)
				transMinY(false, CURVE);
		}
		if (b.containsKey("translateZFactorC"))
			translateZFactorC = ((Integer) b.get("translateZFactorC")).intValue();
		if (translateZFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorC; tPlusCnt++)
				transPlusZ(false, CURVE);
		}
		if (translateZFactorC < 0)
		{	for (int tMinCnt = translateZFactorC; tMinCnt < 0; tMinCnt++)
				transMinZ(false, CURVE);
		}

		if (b.containsKey("noAxesC"))
			noAxesC = ((Boolean) b.get("noAxesC")).booleanValue();
		if (b.containsKey("floorTypeC"))
			floorTypeC = ((Integer) b.get("floorTypeC")).intValue();
		this.noAxesC = noAxesC;
		this.floorTypeC = floorTypeC;
		
		if (b.containsKey("labelTypeC"))
			labelTypeC = ((Integer) b.get("labelTypeC")).intValue();
		this.labelTypeC = labelTypeC;

		if (b.containsKey("centraleProjC"))
			centraleProjC = ((Boolean) b.get("centraleProjC")).booleanValue();
		this.centraleProjC = centraleProjC;
		
		// hier, objectType nodig
		//layoutKnoppenPanel();
		
		
		//functieEditor.setState(b);
	}
	
	public void setEditState(HashMap<String,Object> b)
	{

System.out.println("g3dc setEditState");

		// edit state
		boolean zoomOptie = true;
		boolean translateOptie = true;
		boolean solidDraadKeuzeOptie = true;
		boolean finerKeuzeOptie = true;
		boolean asKeuzeOptie = true;
		boolean labelKeuzeOptie = true;
		boolean projectieKeuzeOptie = true;
		boolean kleurKeuzeOptie = true;

		// functieTypeKeuze in functieEditor
		boolean figuurIsDemo = false;
		
		if (b.containsKey("zoomOptie"))
			zoomOptie = ((Boolean) b.get("zoomOptie")).booleanValue();
		if (b.containsKey("translateOptie"))
			translateOptie = ((Boolean) b.get("translateOptie")).booleanValue();
		if (b.containsKey("solidDraadKeuzeOptie"))
			solidDraadKeuzeOptie = ((Boolean) b.get("solidDraadKeuzeOptie")).booleanValue();
		if (b.containsKey("finerKeuzeOptie"))
			finerKeuzeOptie = ((Boolean) b.get("finerKeuzeOptie")).booleanValue();
		if (b.containsKey("asKeuzeOptie"))
			asKeuzeOptie = ((Boolean) b.get("asKeuzeOptie")).booleanValue();
		if (b.containsKey("labelKeuzeOptie"))
			labelKeuzeOptie = ((Boolean) b.get("labelKeuzeOptie")).booleanValue();
		if (b.containsKey("projectieKeuzeOptie"))
			projectieKeuzeOptie = ((Boolean) b.get("projectieKeuzeOptie")).booleanValue();
		if (b.containsKey("kleurKeuzeOptie"))
			kleurKeuzeOptie = ((Boolean) b.get("kleurKeuzeOptie")).booleanValue();

		if (b.containsKey("figuurIsDemo"))
			figuurIsDemo = ((Boolean) b.get("figuurIsDemo")).booleanValue();

		zetZoomOptie(zoomOptie);
		zetTranslateOptie(translateOptie);
		zetSolidDraadKeuzeOptie(solidDraadKeuzeOptie);
		zetFinerKeuzeOptie(finerKeuzeOptie);
		zetAsKeuzeOptie(asKeuzeOptie);
		zetLabelKeuzeOptie(labelKeuzeOptie);
		zetProjectieKeuzeOptie(projectieKeuzeOptie);
		zetKleurKeuzeOptie(kleurKeuzeOptie);
		
		this.figuurIsDemo = figuurIsDemo;
		//zetFiguurIsDemo(figuurIsDemo);		
		
		// state
		int objectType = FUNCTION;
		
		double angleXG = Object3DContainer.angleXStart;
		double angleZG = Object3DContainer.angleZStart;
		double angleXS = Object3DContainer.angleXStart;
		double angleZS = Object3DContainer.angleZStart;
		double angleXC = Object3DContainer.angleXStart;
		double angleZC = Object3DContainer.angleZStart;
		
		int zoomFactorG = 0;
		int translateXFactorG = 0;
		int translateYFactorG = 0;
		int translateZFactorG = 0;
		
		int zoomFactorS = 0;
		int translateXFactorS = 0;
		int translateYFactorS = 0;
		int translateZFactorS = 0;

		int zoomFactorC = 0;
		int translateXFactorC = 0;
		int translateYFactorC = 0;
		int translateZFactorC = 0;
		
		boolean wireFrameG = false;
		boolean wireFrameS = false;
		
		int finerFactorG = 0;
		
		boolean noAxesG = false;
		boolean noAxesS = false;
		boolean noAxesC = false;
		
		int floorTypeG = NOFLOOR;
		int floorTypeS = NOFLOOR;
		int floorTypeC = NOFLOOR;
		
	    int labelTypeG = ENDLABELS;
	    int labelTypeS = ENDLABELS;
	    int labelTypeC = ENDLABELS;
	    
	    boolean centraleProjG = true;
	    boolean centraleProjS = true;
	    boolean centraleProjC = true;
	    
	    CssColor graphColor = transYellow;
	    CssColor surfaceColor = transYellow;
	    
		
		if (b.containsKey("objectType"))
			objectType = ((Integer) b.get("objectType")).intValue();
		this.objectType = objectType;

//System.out.println("ot = " + this.objectType);		
		// FUNCTION
		if (b.containsKey("angleXG"))
			angleXG = ((Double) b.get("angleXG")).doubleValue();
		if (b.containsKey("angleZG"))
			angleZG = ((Double) b.get("angleZG")).doubleValue();
		this.angleXG = angleXG;
		this.angleZG = angleZG;
		
		if (b.containsKey("zoomFactorG"))
			zoomFactorG = ((Integer) b.get("zoomFactorG")).intValue();
		if (zoomFactorG < 0)
		{	for (int zUitCnt = zoomFactorG; zUitCnt < 0; zUitCnt++)
				zoomUit(false, FUNCTION);
		}
		if (zoomFactorG > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorG; zInCnt++)
			zoomIn(false, FUNCTION);
		}
		
		if (b.containsKey("translateXFactorG"))
			translateXFactorG = ((Integer) b.get("translateXFactorG")).intValue();
		if (translateXFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorG; tPlusCnt++)
				transPlusX(false, FUNCTION);
		}
		if (translateXFactorG < 0)
		{	for (int tMinCnt = translateXFactorG; tMinCnt < 0; tMinCnt++)
				transMinX(false, FUNCTION);
		}
		if (b.containsKey("translateYFactorG"))
			translateYFactorG = ((Integer) b.get("translateYFactorG")).intValue();
		if (translateYFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorG; tPlusCnt++)
				transPlusY(false, FUNCTION);
		}
		if (translateYFactorG < 0)
		{	for (int tMinCnt = translateYFactorG; tMinCnt < 0; tMinCnt++)
				transMinY(false, FUNCTION);
		}
		if (b.containsKey("translateZFactorG"))
			translateZFactorG = ((Integer) b.get("translateZFactorG")).intValue();
		if (translateZFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorG; tPlusCnt++)
				transPlusZ(false, FUNCTION);
		}
		if (translateZFactorG < 0)
		{	for (int tMinCnt = translateZFactorG; tMinCnt < 0; tMinCnt++)
				transMinZ(false, FUNCTION);
		}
		
		if (b.containsKey("wireFrameG"))
		{	wireFrameG = ((Boolean) b.get("wireFrameG")).booleanValue();
		}
		this.wireFrameG = wireFrameG;
		
		if (b.containsKey("finerFactorG"))
			finerFactorG = ((Integer) b.get("finerFactorG")).intValue();
		if (finerFactorG > 0)
		{	for (int fPlusCnt = 0; fPlusCnt < finerFactorG; fPlusCnt++)
				zetFijner(false, FUNCTION);
		}
		if (finerFactorG < 0)
		{	for (int fMinCnt = finerFactorG; fMinCnt < 0; fMinCnt++)
				zetGrover(false, FUNCTION);
		}
		
		if (b.containsKey("noAxesG"))
			noAxesG = ((Boolean) b.get("noAxesG")).booleanValue();
		if (b.containsKey("floorTypeG"))
			floorTypeG = ((Integer) b.get("floorTypeG")).intValue();
		this.noAxesG = noAxesG;
		this.floorTypeG = floorTypeG;

		if (b.containsKey("labelTypeG"))
			labelTypeG = ((Integer) b.get("labelTypeG")).intValue();
		this.labelTypeG = labelTypeG;
		
		if (b.containsKey("centraleProjG"))
			centraleProjG = ((Boolean) b.get("centraleProjG")).booleanValue();
		this.centraleProjG = centraleProjG;
		
		if (b.containsKey("graphColor"))
			graphColor = (CssColor) b.get("graphColor");
		this.graphColor = graphColor;
		
		// SURFACE
		if (b.containsKey("angleXS"))
			angleXS = ((Double) b.get("angleXS")).doubleValue();
		if (b.containsKey("angleZS"))
			angleZS = ((Double) b.get("angleZS")).doubleValue();
		this.angleXS = angleXS;
		this.angleZS = angleZS;
		
		if (b.containsKey("zoomFactorS"))
			zoomFactorS = ((Integer) b.get("zoomFactorS")).intValue();
		if (zoomFactorS < 0)
		{	for (int zUitCnt = zoomFactorS; zUitCnt < 0; zUitCnt++)
				zoomUit(false, SURFACE);
		}
		if (zoomFactorS > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorS; zInCnt++)
			zoomIn(false, SURFACE);
		}
		
		if (b.containsKey("translateXFactorS"))
			translateXFactorS = ((Integer) b.get("translateXFactorS")).intValue();
		if (translateXFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorS; tPlusCnt++)
				transPlusX(false, SURFACE);
		}
		if (translateXFactorS < 0)
		{	for (int tMinCnt = translateXFactorS; tMinCnt < 0; tMinCnt++)
				transMinX(false, SURFACE);
		}
		if (b.containsKey("translateYFactorS"))
			translateYFactorS = ((Integer) b.get("translateYFactorS")).intValue();
		if (translateYFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorS; tPlusCnt++)
				transPlusY(false, SURFACE);
		}
		if (translateYFactorS < 0)
		{	for (int tMinCnt = translateYFactorS; tMinCnt < 0; tMinCnt++)
				transMinY(false, SURFACE);
		}
		if (b.containsKey("translateZFactorS"))
			translateZFactorS = ((Integer) b.get("translateZFactorS")).intValue();
		if (translateZFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorS; tPlusCnt++)
				transPlusZ(false, SURFACE);
		}
		if (translateZFactorS < 0)
		{	for (int tMinCnt = translateZFactorS; tMinCnt < 0; tMinCnt++)
				transMinZ(false, SURFACE);
		}
		
		if (b.containsKey("wireFrameS"))
			wireFrameS = ((Boolean) b.get("wireFrameS")).booleanValue();
		this.wireFrameS = wireFrameS;

		if (b.containsKey("noAxesS"))
			noAxesS = ((Boolean) b.get("noAxesS")).booleanValue();
		if (b.containsKey("floorTypeS"))
			floorTypeS = ((Integer) b.get("floorTypeS")).intValue();
		this.noAxesS = noAxesS;
		this.floorTypeS = floorTypeS;

		if (b.containsKey("labelTypeS"))
			labelTypeS = ((Integer) b.get("labelTypeS")).intValue();
		this.labelTypeS = labelTypeS;
		
		if (b.containsKey("centraleProjS"))
			centraleProjS = ((Boolean) b.get("centraleProjS")).booleanValue();
		this.centraleProjS = centraleProjS;
		
		if (b.containsKey("surfaceColor"))
			surfaceColor = (CssColor) b.get("surfaceColor");
		this.surfaceColor = surfaceColor;
		
		// CURVE
		if (b.containsKey("angleXC"))
			angleXC = ((Double) b.get("angleXC")).doubleValue();
		if (b.containsKey("angleZC"))
			angleZC = ((Double) b.get("angleZC")).doubleValue();
		this.angleXC = angleXC;
		this.angleZC = angleZC;
		
		if (b.containsKey("zoomFactorC"))
			zoomFactorC = ((Integer) b.get("zoomFactorC")).intValue();
		if (zoomFactorC < 0)
		{	for (int zUitCnt = zoomFactorC; zUitCnt < 0; zUitCnt++)
				zoomUit(false, CURVE);
		}
		if (zoomFactorC > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorC; zInCnt++)
			zoomIn(false, CURVE);
		}
		
		if (b.containsKey("translateXFactorC"))
			translateXFactorC = ((Integer) b.get("translateXFactorC")).intValue();
		if (translateXFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorC; tPlusCnt++)
				transPlusX(false, CURVE);
		}
		if (translateXFactorC < 0)
		{	for (int tMinCnt = translateXFactorC; tMinCnt < 0; tMinCnt++)
				transMinX(false, CURVE);
		}
		if (b.containsKey("translateYFactorC"))
			translateYFactorC = ((Integer) b.get("translateYFactorC")).intValue();
		if (translateYFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorC; tPlusCnt++)
				transPlusY(false, CURVE);
		}
		if (translateYFactorC < 0)
		{	for (int tMinCnt = translateYFactorC; tMinCnt < 0; tMinCnt++)
				transMinY(false, CURVE);
		}
		if (b.containsKey("translateZFactorC"))
			translateZFactorC = ((Integer) b.get("translateZFactorC")).intValue();
		if (translateZFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorC; tPlusCnt++)
				transPlusZ(false, CURVE);
		}
		if (translateZFactorC < 0)
		{	for (int tMinCnt = translateZFactorC; tMinCnt < 0; tMinCnt++)
				transMinZ(false, CURVE);
		}

		if (b.containsKey("noAxesC"))
			noAxesC = ((Boolean) b.get("noAxesC")).booleanValue();
		if (b.containsKey("floorTypeC"))
			floorTypeC = ((Integer) b.get("floorTypeC")).intValue();
		this.noAxesC = noAxesC;
		this.floorTypeC = floorTypeC;
		
		if (b.containsKey("labelTypeC"))
			labelTypeC = ((Integer) b.get("labelTypeC")).intValue();
		this.labelTypeC = labelTypeC;

		if (b.containsKey("centraleProjC"))
			centraleProjC = ((Boolean) b.get("centraleProjC")).booleanValue();
		this.centraleProjC = centraleProjC;
		
		// hier, objectType nodig
		//layoutKnoppenPanel();
		
		
		//functieEditor.setEditState(b);
		
		//zetDraadFiguur(objectType);
	}
	
	public HashMap<String,Object> getState()
	{
		//Hashtable h = functieEditor.getState();
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		// state
		h.put("objectType", new Integer(objectType));
		
		// update de hoeken van het laatste functieType
		getHoeken();
		
		h.put("angleXG", new Double(angleXG));
		h.put("angleZG", new Double(angleZG));
		h.put("angleXS", new Double(angleXS));
		h.put("angleZS", new Double(angleZS));		
		h.put("angleXC", new Double(angleXC));
		h.put("angleZC", new Double(angleZC));
		
		h.put("zoomFactorG", new Integer(zoomFactorG));
		h.put("translateXFactorG", new Integer(translateXFactorG));
		h.put("translateYFactorG", new Integer(translateYFactorG));
		h.put("translateZFactorG", new Integer(translateZFactorG));
		
		h.put("zoomFactorS", new Integer(zoomFactorS));
		h.put("translateXFactorS", new Integer(translateXFactorS));
		h.put("translateYFactorS", new Integer(translateYFactorS));
		h.put("translateZFactorS", new Integer(translateZFactorS));

		h.put("zoomFactorC", new Integer(zoomFactorC));
		h.put("translateXFactorC", new Integer(translateXFactorC));
		h.put("translateYFactorC", new Integer(translateYFactorC));
		h.put("translateZFactorC", new Integer(translateZFactorC));
		
		h.put("wireFrameG", new Boolean(wireFrameG));
		h.put("wireFrameS", new Boolean(wireFrameS));
		
		h.put("finerFactorG", new Integer(finerFactorG));
		
		h.put("noAxesG", new Boolean(noAxesG));
		h.put("noAxesS", new Boolean(noAxesS));
		h.put("noAxesC", new Boolean(noAxesC));
		
		h.put("floorTypeG", new Integer(floorTypeG));
		h.put("floorTypeS", new Integer(floorTypeS));
		h.put("floorTypeC", new Integer(floorTypeC));
		
		h.put("labelTypeG", new Integer(labelTypeG));
		h.put("labelTypeS", new Integer(labelTypeS));
		h.put("labelTypeC", new Integer(labelTypeC));
		
		h.put("centraleProjG", new Boolean(centraleProjG));
		h.put("centraleProjS", new Boolean(centraleProjS));
		h.put("centraleProjC", new Boolean(centraleProjC));
		
		h.put("graphColor", graphColor);
		h.put("surfaceColor", surfaceColor);
		
		return h;
	}
	
	public HashMap<String,Object> getEditState()
	{
		
System.out.println("g3dc getEditState");

		//Hashtable h = functieEditor.getEditState();
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		// edit state
		h.put("zoomOptie", new Boolean(zoomOptie));
		h.put("translateOptie", new Boolean(translateOptie));
		h.put("solidDraadKeuzeOptie", new Boolean(solidDraadKeuzeOptie));
		h.put("finerKeuzeOptie", new Boolean(finerKeuzeOptie));
		h.put("asKeuzeOptie", new Boolean(asKeuzeOptie));
		h.put("labelKeuzeOptie", new Boolean(labelKeuzeOptie));
		h.put("projectieKeuzeOptie", new Boolean(projectieKeuzeOptie));
		h.put("kleurKeuzeOptie", new Boolean(kleurKeuzeOptie));
		h.put("figuurIsDemo", new Boolean(figuurIsDemo));

		// state
		h.put("objectType", new Integer(objectType));
		
		// update de hoeken van het laatste functieType
		getHoeken();
		
		h.put("angleXG", new Double(angleXG));
		h.put("angleZG", new Double(angleZG));
		h.put("angleXS", new Double(angleXS));
		h.put("angleZS", new Double(angleZS));		
		h.put("angleXC", new Double(angleXC));
		h.put("angleZC", new Double(angleZC));
		
		h.put("zoomFactorG", new Integer(zoomFactorG));
		h.put("translateXFactorG", new Integer(translateXFactorG));
		h.put("translateYFactorG", new Integer(translateYFactorG));
		h.put("translateZFactorG", new Integer(translateZFactorG));
		
		h.put("zoomFactorS", new Integer(zoomFactorS));
		h.put("translateXFactorS", new Integer(translateXFactorS));
		h.put("translateYFactorS", new Integer(translateYFactorS));
		h.put("translateZFactorS", new Integer(translateZFactorS));

		h.put("zoomFactorC", new Integer(zoomFactorC));
		h.put("translateXFactorC", new Integer(translateXFactorC));
		h.put("translateYFactorC", new Integer(translateYFactorC));
		h.put("translateZFactorC", new Integer(translateZFactorC));
		
		h.put("wireFrameG", new Boolean(wireFrameG));
		h.put("wireFrameS", new Boolean(wireFrameS));
//System.out.println("wfG = " + wireFrameG);		
		h.put("finerFactorG", new Integer(finerFactorG));
		
		h.put("noAxesG", new Boolean(noAxesG));
		h.put("noAxesS", new Boolean(noAxesS));
		h.put("noAxesC", new Boolean(noAxesC));
		
		h.put("floorTypeG", new Integer(floorTypeG));
		h.put("floorTypeS", new Integer(floorTypeS));
		h.put("floorTypeC", new Integer(floorTypeC));
		
		h.put("labelTypeG", new Integer(labelTypeG));
		h.put("labelTypeS", new Integer(labelTypeS));
		h.put("labelTypeC", new Integer(labelTypeC));
		
		h.put("centraleProjG", new Boolean(centraleProjG));
		h.put("centraleProjS", new Boolean(centraleProjS));
		h.put("centraleProjC", new Boolean(centraleProjC));
		
		h.put("graphColor", graphColor);
		h.put("surfaceColor", surfaceColor);
		
		return h;
	}
    
}
