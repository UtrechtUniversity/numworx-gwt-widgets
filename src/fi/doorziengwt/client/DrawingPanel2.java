package fi.doorziengwt.client;


import java.util.*;
import java.io.Serializable;

//import javax.swing.*;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.dom.client.Style;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import fi.doorziengwt.client.DrawingShell.TouchHandler;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;


// class for main drawing area
// a Panel containing one or more objects to be drawn in
// such as an Object3DContainer or others
// also contains all control routines
public class DrawingPanel2 extends LayoutPanel
{   // applet frame
    DoorzienGWT owner;

    // the 3D panel(s)
    Object3DContainer panel3D;// = new Object3DContainer();
    Object3DContainer cutPanel;// = new Object3DContainer();
    
    int modelCode = DoorzienGWT.CUBE;

    public static double MAXZOOM = 15e-1d;
    public static double MINZOOM = 2e-1d; 
    public static double ZOOMSTEP = 1e-1d;
    public static double defaultZoom = 8e-1d;
    public double zoom = defaultZoom;
    // projections
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    public int projection = CENTRALPROJ;
    
    // object is wireframe/solid
    boolean filled = false;
    // fill planes
    boolean planesFilled = false;
    // show cut
    boolean showCut = false;
    // figure is cut
    boolean figureCut = false;

    // mouse modes
    // lines
    public static final int INERT = 0;
    public static final int DRAWLINE = 1;
    public static final int DELETELINE = 2;
    
    // planes
    public static final int DRAWPLANE = 5;
    // voor EPN
    public static final int DRAWPARPLANE = 12;    
    
    public static final int DELETEPLANE = 6;
    public static final int ROTATEPLANE = 7;
    public static final int TRANSLATEPLANE = 8;
    public static final int SHOWHIDECUT = 9; 
    public static final int CUTOBJECT = 10;    
    
    // fold out
    public static final int FOLDOUT = 11;        

    // default for mouseMode
    public int mouseMode = INERT;
    public int oldMouseMode;
    
    // listener for mouse movements on panel3D
    //MLMML listener;

    // drawing
    boolean startUp = false;//true;
    //Image offscreen = null;

    // code of originalObject
//    int modelCode = 0;
    // the object (not group) being studied
    Object3D originalObject;
    // the objectgroup being manipulated
    // after successfully finishing the manipulation update
    ObjectGroup3D currentObjectGroup;
    // temporary version of object being studied, nodig?
    Object3D tempOrigObject, tempOrigObject2;
    // a temporary objectgroup in case we abort the manipulation
    ObjectGroup3D tempObjectGroup, tempObjectGroup2;    
    // two halves if object was cut
    ObjectGroup3D cutObjectGroup;

    // drawing lines and planes
    int pointsSelected = 0;
    Vector3D point1, point2, point3;
    Vector3D movedPoint, clickedPoint;
    Vector3D[] movedEdgeWithPoint, clickedEdgeWithPoint;
    
    Line3D lineChoosen;
    Plane3D planeChoosen;

    Vector construction, transRotConstruct;
    Vector constructionColors;

    Plane3D rotPlane, rotPlaneChoosen;
    Line3D rotLine;
    //Vector3D[] rotEdgeWithPoint;
    
    double minRot, maxRot;
    Plane3D transPlane, transPlaneChoosen;
    double minTrans, maxTrans;

    Plane3D parPlaneChoosen;
    Vector3D parPointChoosen;

    Plane3D cutPlane, cutPlaneChoosen;
    boolean oldPlanesFilled;

    Facet3D facetChoosen;
    
    int numLines = 0;
    int numPlanes = 0;

    // lengthening lines
    // percentwise or absolute?
    public static double MAXLLFACTOR = 3;
    public static double LLSTEP = 2e-1d;
//    public static double llFactor = 0;

    // making a foldout    
    Object3D foldOutObject;
    ObjectGroup3D foldOutObjectGroup;
    Facet3D startFacet = null;
    boolean[] facetsUsed;
    FoldOutTreeNode foldOutTreeRoot;
    // initial fold out factor
    double foldOutInit = 2e-1d;
    // current fold out factor
    double currentFoldOut;
    // filling at foldout
    boolean oldFilled;
    // flattening
    boolean flattened = false;
    // position of whole figure at foldout
    Matrix3D oldPos;
    // zoom at foldout
//    double oldZoom;

    // using the slider
    double sliderValue = 0;
    Slider2 slider;

  
    PushButton flatButton;
    
    // tick modes
    public static int NOTICKS = 0;
    public static int INHERITED = 1;
    public static int INDIVIDUAL = 2;
    public int tickMode = NOTICKS;

    //public static int TICKNUM = 0;    
    //public static boolean TICKSVISIBLE = false;
    
//GWT    
    //public DropButton dropButton;
//GWT    
//    public LWPopUp2 dropMenu;
    
    public ListBox dropBox;
    
    // undo
    Vector history = new Vector();
    public static int MAXHISTORY = 20;
    public int historyPointer = 0;
    
    // managing the cursor
    // coordinates
    int xClicked;
    int yClicked;
    int xMoved;
    int yMoved;
    // circle radius for rotate modes
    public static double RADFACTOR = 1d;
    // managing the crosshair
    boolean helpPoint = false;
    // using the preview
    boolean previewOn = false;
    
    //MLMML ml;
    //CutMLMML cutml;
    
    // voor de demo
    boolean draaibaar = true;
    
// for testing
String testString = "";
// font for testing
//Font fo = new Font("Helvetica", Font.PLAIN, 11);
//GWT
//FontMetrics fm = getFontMetrics(fo);


	Canvas drawingPanelCanvas;
	Context2d drawingPanelContext2d;
	int breedte, hoogte;
	Canvas cutPanelCanvas;
	Context2d cutPanelContext2d;
	
	boolean dragging = false;
    boolean inCircle = false;
    int xStart, yStart;
	
// for testing speed

public static Date date;
public static long startTime;
public static long endTime;

public static void setStart()
{   date = new Date();
    startTime = date.getTime();
}    

public static long getTime()
{   date = new Date();
    endTime = date.getTime();
    return (endTime - startTime);
}    

public static void showTime(String comment)
{   System.out.println(comment + " " + getTime());
    
}    

    // constructor    
    public DrawingPanel2(DoorzienGWT o, int b, int h, int startModel)
    {   owner = o;
    
    	breedte = b;
    	hoogte = h;
    
    	drawingPanelCanvas = Canvas.createIfSupported();
    	drawingPanelCanvas.setWidth(breedte + "px");
    	drawingPanelCanvas.setHeight(hoogte + "px");
    	drawingPanelCanvas.setCoordinateSpaceWidth(breedte);
    	drawingPanelCanvas.setCoordinateSpaceHeight(hoogte);

    	drawingPanelCanvas.addStyleName(owner.doorzienGWTCss.canvas());
    	
    	add(drawingPanelCanvas);
    	setWidgetLeftWidth(drawingPanelCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(drawingPanelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		drawingPanelContext2d = drawingPanelCanvas.getContext2d();
    	
    	MouseHandler mouseHandler = new MouseHandler();
    	drawingPanelCanvas.addMouseDownHandler(mouseHandler);
    	drawingPanelCanvas.addMouseMoveHandler(mouseHandler);
    	drawingPanelCanvas.addMouseUpHandler(mouseHandler);

      	TouchHandler touchHandler = new TouchHandler();
      	drawingPanelCanvas.addTouchStartHandler(touchHandler);
      	drawingPanelCanvas.addTouchMoveHandler(touchHandler);
      	drawingPanelCanvas.addTouchEndHandler(touchHandler);
    	
    
      	cutPanelCanvas = Canvas.createIfSupported();
      	cutPanelCanvas.setWidth(breedte/2 + "px");
      	cutPanelCanvas.setHeight(hoogte + "px");
      	cutPanelCanvas.setCoordinateSpaceWidth(breedte/2);
      	cutPanelCanvas.setCoordinateSpaceHeight(hoogte);

      	cutPanelCanvas.addStyleName(DoorzienGWT.doorzienGWTCss.canvas());
    	
      	CutMouseHandler cutMouseHandler = new CutMouseHandler();
      	cutPanelCanvas.addMouseDownHandler(cutMouseHandler);
      	cutPanelCanvas.addMouseMoveHandler(cutMouseHandler);
      	cutPanelCanvas.addMouseUpHandler(cutMouseHandler);

      	CutTouchHandler cutTouchHandler = new CutTouchHandler();
      	cutPanelCanvas.addTouchStartHandler(cutTouchHandler);
      	cutPanelCanvas.addTouchMoveHandler(cutTouchHandler);
      	cutPanelCanvas.addTouchEndHandler(cutTouchHandler);
      	
    	//add(cutPanelCanvas);
    	//setWidgetLeftWidth(cutPanelCanvas, 0, Style.Unit.PX, breedte/2, Style.Unit.PX);
		//setWidgetTopHeight(cutPanelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		//cutPanelCanvas.setVisible(false);
		//cutPanelContext2d = drawingPanelCanvas.getContext2d();
    	
    	
    	panel3D = new Object3DContainer(drawingPanelContext2d, breedte, hoogte);
//GWT    	
    	//cutPanel = new Object3DContainer(cutPanelContext2d, breedte/2, hoogte);
        
// ALLEEN VOOR FI        
        if (DoorzienGWT.version == DoorzienGWT.FI)
            previewOn = true;
            
//GWT
/*        
        dropButton = new DropButton(owner.tt("divideSidesText"), 20);
        dropButton.setLocation(panel3D.getSize().width - 
                               dropButton.getSize().width, 0);
        dropButton.addMouseListener(new DropML());
*/        
                               
//GWT
/*        
        dropMenu = new LWPopUp2(this, "", owner.dropNumHelpPoints);
        dropMenu.setCheckable(true);
        dropMenu.setLocation(panel3D.getSize().width - 
                             dropMenu.getSize().width, 
                             dropButton.getSize().height);
*/                             
        
/*        
        dropBox = new ListBox();
        dropBox.addItem("verdeel in");
        dropBox.addItem("2 delen");
        dropBox.addItem("3 delen");
        dropBox.addItem("4 delen");
        dropBox.addItem("5 delen");
        dropBox.addItem("6 delen");
        
        add(dropBox);
    	setWidgetLeftWidth(dropBox, breedte - 90, Style.Unit.PX, 90, Style.Unit.PX);
		setWidgetTopHeight(dropBox, 0, Style.Unit.PX, 20, Style.Unit.PX);
		dropBox.setVisible(false);
		
		dropBox.addChangeHandler(new ListChangeHandler());
*/		
		
		slider = new Slider2(this, 0, 1);
		add(slider.sliderCanvas);
    	setWidgetLeftWidth(slider.sliderCanvas, breedte - Slider2.horSize - 1, Style.Unit.PX, Slider2.horSize, Style.Unit.PX);
		setWidgetTopHeight(slider.sliderCanvas, 1, Style.Unit.PX, Slider2.vertSize, Style.Unit.PX);
		slider.setVisible(false);
		
		flatButton = new PushButton("plat");
		add(flatButton);
    	setWidgetLeftWidth(flatButton, breedte - 40 - 1, Style.Unit.PX, 40, Style.Unit.PX);
		setWidgetTopHeight(flatButton, 1 + Slider2.vertSize, Style.Unit.PX, 22, Style.Unit.PX);
		flatButton.setVisible(false);
		flatButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		flatButton.addClickHandler(new FlatCL());
		
		
        
//GWT                                     
//        cutml = new CutMLMML();
//        cutPanel.addMouseListener(cutml);
//        cutPanel.addMouseMotionListener(cutml);        
        
        setNewModel(startModel);
        
    }  // constructor  

    public void zetDraaibaar(boolean b)
    {   draaibaar = b; 
    }
    
    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            projection = CENTRALPROJ;
        if (proj == PARALLELPROJ)    
            projection = PARALLELPROJ;    
        panel3D.setProjection(projection);
        // just in case
        if (cutPanel != null)
            cutPanel.setProjection(projection);
    }
    public void setLetters(boolean b)
    {   DrawConstants.letters = b;
        panel3D.repaint();
        if (cutPanel != null)
            cutPanel.repaint();
    }
    
    public void setHelpPoints(int num)
    {   DrawConstants.TICKNUM = num;
    
        // rebuild currentObjectGroup    
        // this is never an object with point
        boolean currentVisible = (panel3D.model == currentObjectGroup);
        construction = new Vector();
        if (currentObjectGroup instanceof ObjectWithLine)
            construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
        else if (currentObjectGroup instanceof ObjectWithPlane)
            construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
        originalObject.setTickMarks(num);
        currentObjectGroup = rebuild(originalObject, construction, null);
        originalObject = currentObjectGroup.leftMostLeaf();
        fillPlanes(planesFilled);        
        if (currentVisible)
            panel3D.initializeModel(currentObjectGroup, false);        
        
        // rebuild tempObjectGroup
        if (tempObjectGroup != null)
        {   boolean tempVisible = 
                (panel3D.model == tempObjectGroup);
            Object3D tempStart = tempObjectGroup.leftMostLeaf();
            Vector tempConstruction = new Vector();
            Vector tempConstructionColors = new Vector();
            if (tempObjectGroup instanceof ObjectWithLine)
                tempConstruction = ((ObjectWithLine) tempObjectGroup).getConstruction();
            else if (tempObjectGroup instanceof ObjectWithPlane)
                tempConstruction = ((ObjectWithPlane) tempObjectGroup).getConstruction();
            else if (tempObjectGroup instanceof ObjectWithPoint)
                tempConstruction = ((ObjectWithPoint) tempObjectGroup).getConstruction();
            
            if (tempObjectGroup instanceof ObjectWithLine)
                tempConstructionColors = ((ObjectWithLine) tempObjectGroup).getConstructionColors();
            else if (tempObjectGroup instanceof ObjectWithPlane)
                tempConstructionColors = ((ObjectWithPlane) tempObjectGroup).getConstructionColors();
            else if (tempObjectGroup instanceof ObjectWithPoint)
                tempConstructionColors = ((ObjectWithPoint) tempObjectGroup).getConstructionColors();
            
            tempStart.setTickMarks(num);
            tempObjectGroup = rebuild(tempStart, tempConstruction, tempConstructionColors);
            tempOrigObject = tempObjectGroup.leftMostLeaf();
            fillPlanes(planesFilled);        
            if (tempVisible)
                panel3D.initializeModel(tempObjectGroup, false);        
            
        
        }
// voor FI nog tempObjectGroup2        
    
    }
    
    // switching on and off from the MENU
    // default on is divide in two parts
    public void setHelpPointDrop(boolean b)
    {   
    	

   	
    	if (b)
        {   setHelpPoints(1);
//            dropMenu.switchTo(owner.tt("twoPartsText"));
            if ((mouseMode == DRAWLINE) ||
                (mouseMode == DRAWPLANE) ||
                (mouseMode == DRAWPARPLANE)
               )
            {   //dropBox.setVisible(true);
            	//panel3D.add(dropButton);
                //panel3D.repaint();
            }    
        }
        else
        {   setHelpPoints(0);
        	//dropBox.setVisible(false);
            //panel3D.remove(dropMenu);
            //panel3D.remove(dropButton);
            //panel3D.repaint();
        }    
   
    }
    
    // showing the drop button etc.
    public void showHelpPointDrop(boolean b)
    {   
  
    	if (b)
        {   if (DrawConstants.TICKNUM > 0)
            {
        		//dropBox.setVisible(true);
                //if (dropButton != null)
                //    panel3D.add(dropButton);
                //panel3D.repaint();
            }
        }
        else
        {   
        	//dropBox.setVisible(false);
        	
        	//if (dropMenu != null)
            //    panel3D.remove(dropMenu);
            //if (dropButton != null)    
            //    panel3D.remove(dropButton);
            //panel3D.repaint();
        }
            
    
    }
    
    // changing the model to a new one
    public void setNewModel(int modelCode)
    {   
		// algemene reset
		// boven
        owner.topToolBar.resetDefaults();
        numLines = 0; 
        DrawConstants.llFactor = 0;
        numPlanes = 0;
        planesFilled = false;
        // cutPanel3D verwijderen                        
        if (showCut)
            setCutPanel(false);
        showCut = false;
        figureCut = false;
        // slider verwijderen
        setSlider(false, 0, 0, 1);

		// rechts
        owner.rightToolBar.resetDefaults();

        // enable options
//GWT        
//        owner.enableOptions(true);
        DrawConstants.letters = false;
        
        owner.resetLetters();

		// projectie
        //if (DoorzienDWO.version == DoorzienDWO.EPN)
        //    setProjection(PARALLELPROJ);        
        //else
            setProjection(CENTRALPROJ);
            
        owner.resetProjection();
        
        // hulppunten
        DrawConstants.TICKNUM = 0;
        
        owner.resetHelpPoints();
        showHelpPointDrop(false);
        
        tempObjectGroup = null;
        tempObjectGroup2 = null;
        foldOutObjectGroup = null;
        cutObjectGroup = null;
        
        panel3D.hideHelpLine();                
        panel3D.hideHelpPoint();                
        helpPoint = false;
        panel3D.testString = "";

        mouseMode = INERT;
        history.removeAllElements();        

		// vanaf hier splitsen
		if (modelCode < owner.MYFIGURE)
		{
	        currentObjectGroup = makeNewModel(modelCode);        
    	    // HIER!
	        setFilled(false);        
    	    panel3D.initializeModel(currentObjectGroup, true);

	        // reset zooming HERE
    	    zoom = defaultZoom;
        	panel3D.setZoomFactor(zoom);        
        
        	addToHistory();
        	
//        	owner.helpBar.setText(owner.tt("rotateText"));
            owner.helpBar.setText(TextConstants.rotateText);
        }
        //else if ((modelCode == owner.MYFIGURE) && !startUp)
        //	owner.viewer.setScormedObject3D();	
        
    }    

    public ObjectGroup3D makeNewModel(int code)
    {   modelCode = code;
        Object3D model;
        ObjectGroup3D modelGroup;
        // default?
// binnenvulling is onzichtbaar
// maar voor buitenkant toch NZMINFIRST
// is dit ook OK voor filled = false?
        //panel3D.paintType = Object3DContainer.PUREZ;        
        switch (code)
        {   case (DoorzienGWT.CUBE):
                model = new Box(1, 1, 1, DrawConstants.objectColor);
            break;
            case (DoorzienGWT.BLOCK):
                model = new Box(3, 2, 1, DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.TETRAHEDRON):
                model = new Tetrahedron(DrawConstants.objectColor);
            break;            
            
            case (DoorzienGWT.OCTAHEDRON):
                model = new Octahedron(DrawConstants.objectColor);
            break;            

            case (DoorzienGWT.PIRAMID3):
                model = new Piramid3(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PIRAMID4):
            {   // four sided, height = base edge
                model = new Piramid(1, DrawConstants.objectColor);
            }    
            break;            
            case (DoorzienGWT.PIRAMID5):
                model = new Piramid5(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PIRAMID6):
                model = new Piramid6(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PIRAMID7):
                model = new Piramid7(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PIRAMID8):
                model = new Piramid8(DrawConstants.objectColor);
            break;            
            
            case (DoorzienGWT.PRISM3):
                model = new Prism3(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PRISM4):
                model = new Prism4(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PRISM5):
                model = new Prism5(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.PRISM6):
                model = new Prism6(DrawConstants.objectColor);
            break;            
            
            case (DoorzienGWT.PIRHOUSE):
                model = new PirHouse(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.EDGEHOUSE):
                model = new EdgeHouse(DrawConstants.objectColor);
            break;            
            
            case (DoorzienGWT.DODECAHEDRON):
                model = new Dodecahedron(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.ICOSAHEDRON):
                model = new Icosahedron(DrawConstants.objectColor);
            break;            
            case (DoorzienGWT.CYLINDER):
                model = new Cylinder(2, 5, DrawConstants.objectColor, true, true);
            break;            
            
            case (DoorzienGWT.CONE1):
            {   // foldout 1/4 circle
                // R-bottom = 1/4 * r-foldout
                // r = 4 * R
                // height = sqrt(r^2 - R^2) = 
                // sqrt(15) at R = 1
                model = new Cone(1, Math.sqrt(15), DrawConstants.objectColor, true);
            }    
            break;     
            case (DoorzienGWT.CONE2):
            {   // foldout 1/2 circle
                // R-bottom = 1/2 * r-foldout
                // r = 2 * R
                // height = sqrt(r^2 - R^2) = 
                // sqrt(3) at R = 1
                model = new Cone(1, Math.sqrt(3), DrawConstants.objectColor, true);
            }    
            break;     
            case (DoorzienGWT.CONE3):
            {   // foldout 2/3 circle
                // R-bottom = 2/3 * r-foldout
                // r = (3/2) * R
                // height = sqrt(r^2 - R^2) = 
                // sqrt(5/4) at R = 1
                model = new Cone(1, Math.sqrt(5d / 4), DrawConstants.objectColor, true);
            }    
            break;     
            case (DoorzienGWT.CONE4):
            {   // half top angle 60 degrees
                // height = R / sqrt(3) = 
                // 1 / sqrt(3) at R = 1
                model = new Cone(1, 1 / Math.sqrt(3), DrawConstants.objectColor, true);
            }    
            break;     
            
            
            default: 
                model = new Box(1, 1, 1, DrawConstants.objectColor);
        }
        model.modelCode = code;
        originalObject = model;
//System.out.println("model-numFacets = " + model.numFacets);        
        modelGroup = new ObjectGroup3D(model, false);
        modelGroup.numVertexLabels = model.numVertexLabels;
        return modelGroup;
    }    

    // overloaded
    public ObjectGroup3D makeNewModel(Object3D object)
    {
    	originalObject = object;

    	currentObjectGroup = new ObjectGroup3D(object, false);
    	currentObjectGroup.numVertexLabels = object.numVertexLabels;
        return currentObjectGroup;
    }
    
    public void addToHistory()
    {   int hisSize = history.size();
        ObjectGroup3D og = (ObjectGroup3D) currentObjectGroup.deepCopy();
        // object at historyPointer was changed
        for (int i = hisSize - 1; i > historyPointer; i--)
            history.removeElementAt(i);
        history.addElement(og);            
        historyPointer = history.size() - 1;
        if (history.size() > MAXHISTORY)
        {   history.removeElementAt(0);
            historyPointer--;
        }
        if (history.size() > 1)
            owner.rightToolBar.undoButton.setEnabled(true);
//System.out.println("added, his = " + history.size());                
        owner.rightToolBar.redoButton.setEnabled(false);            
    }
    
    public void previousObjectGroup()
    {   int hisSize = history.size();
//System.out.println("his = " + hisSize);    
        if (hisSize > 1)
        {   historyPointer--;
            //history.removeElementAt(hisSize - 1);
//System.out.println("removed, his = " + history.size());                        
            currentObjectGroup = (ObjectGroup3D) ((ObjectGroup3D) history.elementAt(historyPointer)).deepCopy();
            originalObject = currentObjectGroup.leftMostLeaf();             
            //panel3D.initializeModel(currentObjectGroup, false);
            Vector construction = new Vector();
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            numLines = 0;
            numPlanes = 0;
            for (int i = 0; i < construction.size(); i++)
            {   Object ob = construction.elementAt(i);
                if (ob instanceof Line3D)
                    numLines++;
                if (ob instanceof Plane3D)
                {   numPlanes++;
                }
            }
//System.out.println("numLines = " + numLines);
//System.out.println("numPlanes = " + numPlanes);

            DrawConstants.llFactor = 0;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    DrawConstants.llFactor = ((ObjectWithLine) currentObjectGroup).getLlFactor();
//System.out.println("OWL ll = " + llFactor);                            
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {
            	DrawConstants.llFactor = ((ObjectWithPlane) currentObjectGroup).getLlFactor();
            }    
            
//System.out.println("ll = " + llFactor);                
            originalObject.setTickMarks(DrawConstants.TICKNUM);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();                         
            setNumLines(numLines);
            setNumPlanes(numPlanes);            
            fillPlanes(planesFilled);          
            panel3D.initializeModel(currentObjectGroup, false);
        }
        if (historyPointer == 0)
            owner.rightToolBar.undoButton.setEnabled(false);
        owner.rightToolBar.redoButton.setEnabled(true);            
        
    }    


    public void nextObjectGroup()
    {   int hisSize = history.size();
//System.out.println("his = " + hisSize);    
        if ((hisSize - 1) > historyPointer)
        {   historyPointer++;
            //history.removeElementAt(hisSize - 1);
//System.out.println("removed, his = " + history.size());                        
            currentObjectGroup = (ObjectGroup3D) ((ObjectGroup3D) history.elementAt(historyPointer)).deepCopy();
            originalObject = currentObjectGroup.leftMostLeaf();             
            //panel3D.initializeModel(currentObjectGroup, false);
            Vector construction = new Vector();
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            numLines = 0;
            numPlanes = 0;
            for (int i = 0; i < construction.size(); i++)
            {   Object ob = construction.elementAt(i);
                if (ob instanceof Line3D)
                    numLines++;
                if (ob instanceof Plane3D)
                {   numPlanes++;
                }
            }
//System.out.println("numLines = " + numLines);
//System.out.println("numPlanes = " + numPlanes);

            DrawConstants.llFactor = 0;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    DrawConstants.llFactor = ((ObjectWithLine) currentObjectGroup).getLlFactor();
//System.out.println("OWL ll = " + llFactor);                            
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {
            	DrawConstants.llFactor = ((ObjectWithPlane) currentObjectGroup).getLlFactor();
            }    
//System.out.println("ll = " + llFactor);                
            originalObject.setTickMarks(DrawConstants.TICKNUM);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();                         
            setNumLines(numLines);
            setNumPlanes(numPlanes);           
            fillPlanes(planesFilled);                      
            panel3D.initializeModel(currentObjectGroup, false);
        }
        if ((history.size() - 1) == historyPointer)
            owner.rightToolBar.redoButton.setEnabled(false);
        owner.rightToolBar.undoButton.setEnabled(true);            
        
    }    


    public void setNumLines(int nLines)
    {   numLines = nLines;
        if (nLines == 0)
        {   DrawConstants.llFactor = 0;
            owner.topToolBar.activateLineButtons(false);
        }
        else
        {
            owner.topToolBar.activateLineButtons(true);   
        }    

    }
    
    public void setNumPlanes(int nPlanes)
    {   numPlanes = nPlanes;
        if (nPlanes == 0)
        {   if (showCut)
                killCutPanel();
            planesFilled = false;
            owner.topToolBar.activatePlaneButtons(false);
        }
        else
        {
            owner.topToolBar.activatePlaneButtons(true);   
        }    

    }
    
    public ObjectGroup3D rebuild(Object3D sObject, Vector recipe, Vector colors)
    {   Object3D start = sObject.deepCopy();
        start.setVisible(true);
        // dummy object group
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);   
        startGroup.filled = start.filled; 
        startGroup.numVertexLabels = start.numVertexLabels;
        startGroup.fixFacetArray(); //!!!
        // nu bouwen volgens recipe
        for (int i = 0; i < recipe.size(); i++)
        {   Object ob = recipe.elementAt(i);
            if (ob instanceof Plane3D)
            {   Plane3D pl = (Plane3D) ob;
                int colorIndex = DrawConstants.planeOutlineColorIndex;
                if (colors != null)
                    colorIndex = ((Integer) colors.elementAt(i)).intValue();
                startGroup = new ObjectWithPlane(startGroup, 
                    pl.support, 
                    Vector3D.plus(pl.support, pl.direction1),
                    Vector3D.plus(pl.support, pl.direction2), 
                    colorIndex, true);    
                startGroup.fixFacetArray(); //!!!    
            }
            else if (ob instanceof Line3D)
            {   Line3D li = (Line3D) ob;
                int colorIndex = DrawConstants.lineColorIndex;
                if (colors != null)
                    colorIndex = ((Integer) colors.elementAt(i)).intValue();
                double factor = DrawConstants.llFactor;
                if (colorIndex != DrawConstants.lineColorIndex)
                    factor = 0;
                startGroup = new ObjectWithLine(startGroup, 
                    li.point1, li.point2,
                    colorIndex, factor);    
                startGroup.fixFacetArray(); //!!!
            }    
            else if (ob instanceof Vector3D)
            {   Vector3D vertex = (Vector3D) ob;
                int colorIndex = DrawConstants.pointColorIndex;
                if (colors != null)
                    colorIndex = ((Integer) colors.elementAt(i)).intValue();
                startGroup = new ObjectWithPoint(startGroup,
                    vertex, colorIndex);
                startGroup.fixFacetArray(); //!!!                
                
            }
            else if (ob instanceof EWP)
            {   Vector3D[] edgeWithPoint = ((EWP) ob).edgeWithPoint;
                int colorIndex = DrawConstants.pointColorIndex;
                if (colors != null)
                    colorIndex = ((Integer) colors.elementAt(i)).intValue();
                startGroup = new ObjectWithPoint(startGroup,
                    edgeWithPoint, colorIndex);
                startGroup.fixFacetArray(); //!!!                
                
            }
            
        }
        return startGroup;
// dit kan ook deep copy in OWL en OWP vervangen
// wanneer die methode niet goed werkt
    }
    
    public void setSlider(boolean b, double init, double min, double max)
    {   
//    	if (mouseMode == FOLDOUT)
//    	{
//    		currentFoldOut = init;
//    	}
    	
//GWT
    	
    	if (b)
        {   sliderValue = init;
//System.out.println("initValue = " + UF.format(sliderValue, 2));            
            //slider = new Slider2(this, min, max);
            slider.setMinMax(min, max);
            slider.setPosition(sliderValue);
            if (mouseMode == FOLDOUT)
            {   currentFoldOut = sliderValue;
//            	flatButton = new LWButton(Table.lookUp("flatText"),
//                             30, slider.getSize().height);
//                flatButton.setLocation(
//                    panel3D.getSize().width - flatButton.getSize().width,
//                    0);
             
//                panel3D.add(flatButton);
                // add listener                
//                flatButton.addMouseListener(new FlatML());
                flattened = false;
//                slider.setLocation(
//                    panel3D.getSize().width - slider.getSize().width -
//                        flatButton.getSize().width,
//                    0);    

            }    
            else    
            {   //slider.setLocation(
                //    panel3D.getSize().width - slider.getSize().width,
                //    0);
            }    
            //slider.setVisible(b);
            //panel3D.add(slider);    
            panel3D.repaint();
            
        }
        else
        {   //if (slider != null)
            //    panel3D.remove(slider); 
            //slider = null;    
            //if (flatButton != null)
            //    panel3D.remove(flatButton); 
            //flatButton = null;    
        }
    	slider.setVisible(b);
    	flatButton.setVisible(b);
            
    }
    

    public void processSlider(double newValue)
    {   sliderValue = newValue;
    
//System.out.println("newValue = " + UF.format(newValue, 2));    
        if (mouseMode == FOLDOUT)
        {	currentFoldOut = sliderValue;
            foldOut(foldOutTreeRoot, sliderValue);
            panel3D.initializeModel(foldOutObjectGroup, false);
        }
        else if (mouseMode == ROTATEPLANE)
        {   Plane3D oldRotPlane = rotPlane.copy();
            transRotConstruct.removeElement(rotPlane);
            rotPlane = rotPlaneChoosen.rotateBy(rotLine, newValue);
            if (!transRotConstruct.contains(rotPlane))
            {   transRotConstruct.addElement(rotPlane);
                currentObjectGroup = rebuild(originalObject, transRotConstruct, null);
                originalObject = currentObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);                
                panel3D.initializeModel(currentObjectGroup, false);        
                //fillPlanes(planesFilled);
                if (showCut)
                {    if (oldRotPlane.equals(cutPlane))
                        cutPlane = rotPlane.copy();
                     updateCutPanel();
                
                }
            }
            // else do nothing
            
        }
        else if (mouseMode == TRANSLATEPLANE)
        {   Plane3D oldTransPlane = transPlane.copy();
            transRotConstruct.removeElement(transPlane);
            Vector3D transVec = new Vector3D(transPlaneChoosen.normal);
            Vector3D.scaleBy(transVec, newValue);
            //transVec = Vector3D.plus(transVec, planeChoosen.point);
            transPlane = transPlaneChoosen.translateBy(transVec);
            if (!transRotConstruct.contains(transPlane))
            {   transRotConstruct.addElement(transPlane);
                currentObjectGroup = rebuild(originalObject, transRotConstruct, null);
                originalObject = currentObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);                
                panel3D.initializeModel(currentObjectGroup, false);        
                //fillPlanes(planesFilled);
                if (showCut)
                {    if (oldTransPlane.equals(cutPlane))
                        cutPlane = transPlane.copy();
                     updateCutPanel();
                
                }
            }
            // else do nothing
        }
    }
    
    // note: this is only removing-adding 
    public void setCutPanel(boolean b)
    {   
    	
    	if (b)
    	{
    		drawingPanelCanvas.setWidth(breedte/2 + "px");
        	drawingPanelCanvas.setCoordinateSpaceWidth(breedte/2);
    		setWidgetLeftWidth(drawingPanelCanvas, breedte/2, Style.Unit.PX, breedte / 2, Style.Unit.PX);
    		panel3D.breedte = breedte / 2;
    		panel3D.context2d = drawingPanelCanvas.getContext2d();
    		panel3D.resetModel();
    		
        	add(cutPanelCanvas);
        	setWidgetLeftWidth(cutPanelCanvas, 0, Style.Unit.PX, breedte/2, Style.Unit.PX);
    		setWidgetTopHeight(cutPanelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
          	
    		cutPanelContext2d = cutPanelCanvas.getContext2d();

    		cutPanel = new Object3DContainer(cutPanelContext2d, breedte/2, hoogte);
        	
    	}
    	else
    	{
    		drawingPanelCanvas.setWidth(breedte + "px");
        	drawingPanelCanvas.setCoordinateSpaceWidth(breedte);
    		setWidgetLeftWidth(drawingPanelCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
    		panel3D.breedte = breedte;
    		panel3D.context2d = drawingPanelCanvas.getContext2d();
    		panel3D.resetModel();

    		remove(cutPanelCanvas);
    		
    		
    	}
    	
//GWT
/*    	
        if (b)
        {   
            cutPanel.setBounds(0, 0, getSize().width / 2, getSize().height);        
            panel3D.setBounds(getSize().width / 2, 0, 
                              getSize().width / 2, getSize().height);                    
            cutPanel.resetModel();
            panel3D.resetModel();                              
            //cutPanel.offscreen = null;
            //panel3D.offscreen = null;
            add(cutPanel);        
        }    
        else // remove
        {   remove(cutPanel);
            panel3D.setBounds(0, 0, getSize().width, getSize().height);        
            panel3D.resetModel();    
            //panel3D.offscreen = null;            
        }
        if (slider != null)            
            slider.setLocation(
                panel3D.getSize().width - slider.getSize().width,
                0);
                //panel3D.getSize().height - slider.getSize().height);
        if (dropButton != null)        
            dropButton.setLocation(panel3D.getSize().width - 
                                   dropButton.getSize().width, 0);
        if (dropMenu != null)        
            dropMenu.setLocation(panel3D.getSize().width - 
                                 dropMenu.getSize().width, 
                                 dropButton.getSize().height);
                                   
        
        repaint();
*/        
    }
    // rotate here
    // make sure diameter of flatModel equals diameter
    // of currentObjectGroup
    public void updateCutPanel()
    {   
        ObjectGroup3D flatModel = ObjectWithPlane.getCut(currentObjectGroup, cutPlane);

        cutPanel.mat = new Matrix3D();
        cutPanel.initializeModel(flatModel, false);
        cutPanel.setZoomFactor(zoom);        
    }
    
    public void killCutPanel()
    {   setCutPanel(false);
        showCut = false;
//GWT        
//        owner.topToolBar.showCutButton.setImage(owner.showCutImage);
    }
    
    
    // methods corresponding to buttons
    // right tool bar
    public void setFilled(boolean b)
    {   filled = b;
        currentObjectGroup.setFilled(b);
        if (tempObjectGroup != null)
            tempObjectGroup.setFilled(b);
        if (tempObjectGroup2 != null)
            tempObjectGroup2.setFilled(b);
        if (foldOutObjectGroup != null)
        {    foldOutObjectGroup.setFilled(b);
            // set inside invisible
            for (int p = 0; p < foldOutObjectGroup.numFacets; p++)
            {   Facet3D f = foldOutObjectGroup.facets[p];
                boolean include = true;
                if (foldOutObjectGroup instanceof ObjectWithLine)
                {   ObjectWithLine owl = (ObjectWithLine) foldOutObjectGroup;
                    include = owl.replacesOrigObject(f) && !owl.hasReplacement(f);
                }        
                else if (foldOutObjectGroup instanceof ObjectWithPlane)
                {   ObjectWithPlane owp = (ObjectWithPlane) foldOutObjectGroup;
                    include = owp.replacesOrigObject(f) && !owp.hasReplacement(f);
                }        
                f.visible = include;    
            }
        }
        if (cutObjectGroup != null)
            cutObjectGroup.setFilled(b);
            
        if (filled)
            panel3D.paintType = Object3DContainer.NZMINFIRST;
        else
        {   if (planesFilled && (foldOutObjectGroup == null))
            {   //panel3D.paintType = Object3DContainer.HYBRID2;
                panel3D.paintType = Object3DContainer.SEMIEXACT;
                panel3D.showInside = false;
            }
            else // !planesFilled || (foldOutObjectGroup != null)
            {   panel3D.paintType = Object3DContainer.HYBRID1;            
                panel3D.showInside = true;
            }
        }    
        panel3D.repaint();    
    }    
    
    public void zoomIn()
    {   double temp = zoom + ZOOMSTEP;
        if (temp <= (MAXZOOM + ZOOMSTEP / 10))
        {   zoom = temp;
            panel3D.setZoomFactor(zoom);
            // cutPanel            
            if (showCut)
                cutPanel.setZoomFactor(zoom);
        }    
    }
    
    public void zoomOut()
    {   double temp = zoom - ZOOMSTEP;
        if (temp >= (MINZOOM - ZOOMSTEP / 10))
        {   zoom = temp;
            panel3D.setZoomFactor(zoom);
            // cutPanel            
            if (showCut)
                cutPanel.setZoomFactor(zoom);
            
        }    

    }
    
    public void makeFoldOut(int stepNum, boolean b)
    {   
    	
//System.out.println("make foldout " + stepNum + " " + b);
    	
    	// facet must be choosen
        // pressing the button
        if (stepNum == 0)
        {   
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
            	
//System.out.println("mm = " + mouseMode);

                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                
                owner.topToolBar.showCutButton.setDown(false);                
                
                owner.topToolBar.cutButton.setDown(false);  
                
                //deze NIET!!
                //owner.rightToolBar.conDrawButton.setDown(false);
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;
                foldOutObjectGroup = null;
                cutObjectGroup = null;                
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);
                
                }
                //originalObject = currentObjectGroup.leftMostLeaf();
                // slider weg                                
                setSlider(false, 0, 0, 0);
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                
                DrawConstants.TICKSVISIBLE = false;
                
                showHelpPointDrop(false);
                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            setCutPanel(false);
            // request for whole figure
            if (!b)
            {   
            	
//System.out.println("figure");

            	mouseMode = INERT;
                
            	setCutPanel(showCut);
                
                owner.helpBar.setText(TextConstants.rotateText);
//GWT                
//                owner.enableOptions(true);
                
if (historyPointer > 0)
owner.rightToolBar.undoButton.setEnabled(true);
if (historyPointer < (history.size() - 1))
owner.rightToolBar.redoButton.setEnabled(true);

                setNumLines(numLines);
                setNumPlanes(numPlanes);
                filled = oldFilled;
                setFilled(filled);
                if (filled)
                {    
                	
                	//owner.rightToolBar.wireSolidButton.setImage(owner.wireFrameImage);
                	owner.rightToolBar.wireSolidButton.setDown(true);
                
                }
                else
                {    
                	
                	//owner.rightToolBar.wireSolidButton.setImage(owner.solidImage);
                	owner.rightToolBar.wireSolidButton.setDown(false);
                
                }

                if (oldPos != null)
                {   panel3D.mat.row1 = new Vector3D(oldPos.row1);
                    panel3D.mat.row2 = new Vector3D(oldPos.row2);
                    panel3D.mat.row3 = new Vector3D(oldPos.row3);
                
                }
                panel3D.initializeModel(currentObjectGroup, false);
                
                startFacet = null;
                return;
            }
            mouseMode = FOLDOUT;
            
//System.out.println("foldout");

//GWT
//            owner.enableOptions(false);      
            
            foldOutObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            foldOutObject = foldOutObjectGroup.leftMostLeaf(); 
            
            foldOutObject.loosenVertices();

            //startFacet = null;
            panel3D.initializeModel(foldOutObjectGroup, false);
            
            if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
                (foldOutObject.modelCode == owner.CYLINDER))
            {   // take the frontmost facet of the side
                boolean found = false;
                for (int fCnt = foldOutObject.numFacets - 1; fCnt >= 0; fCnt--)
                {   if (!found && 
                        (foldOutObject.facets[fCnt].numPoints <= 4)
                       ) 
                    {   found = true;
                        startFacet = foldOutObject.facets[fCnt];
                    }
                }
                makeFoldOut(1, true);
                
            }
            else if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
                     ((foldOutObject.modelCode == owner.CONE1) ||
                      (foldOutObject.modelCode == owner.CONE2) ||
                      (foldOutObject.modelCode == owner.CONE3) ||
                      (foldOutObject.modelCode == owner.CONE4)
                     )
                    )

            {   // take the frontmost facet of the side
                boolean found = false;
                for (int fCnt = foldOutObject.numFacets - 1; fCnt >= 0; fCnt--)
                {   if (!found && 
                        (foldOutObject.facets[fCnt].numPoints <= 4)
                       ) 
                    {   found = true;
                        startFacet = foldOutObject.facets[fCnt];
                    }
                }
                makeFoldOut(1, true);
                
            }
            else if (startFacet != null)
            {
//System.out.println("makeFoldout - 0 sf != null");            
            	makeFoldOut(1, true);
            }
            else
            {	
            	
            	  owner.helpBar.setText(TextConstants.conDrawSelectText);
//GWT            	
//                owner.rightToolBar.conDrawButton.setPressed(true);
            	  
                // wait for mouse action
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
            }
            
        }     
        else if (stepNum == 1)
        {   
//GWT        	
//        	owner.rightToolBar.conDrawButton.setPressed(false);
            foldOutObjectGroup.setTickMarks(0);

            helpPoint = false;
            panel3D.hideHelpPoint();

//GWT            
//            owner.enableOptions(false);
            
            owner.topToolBar.disableLineButtons();
            owner.topToolBar.disablePlaneButtons();
owner.rightToolBar.undoButton.setEnabled(false);
owner.rightToolBar.redoButton.setEnabled(false);
            
			// asumed >= 0    
            //int startIndex = foldOutObject.containsFacet(startFacet);    
			int startIndex = NoSer.containsFacet(foldOutObject, startFacet);
            
            
            // init facet labels
            facetsUsed = new boolean[foldOutObject.numFacets]; 
            // create root node, mark facet as labeled
            foldOutTreeRoot = new FoldOutTreeNode(startFacet, 0, 0, 0);
            facetsUsed[startIndex] = true;
            
            if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
                (foldOutObject.modelCode == owner.CYLINDER))
                addTreeCylinderNode(foldOutTreeRoot);            
            else if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
                     ((foldOutObject.modelCode == owner.CONE1) ||
                      (foldOutObject.modelCode == owner.CONE2) ||
                      (foldOutObject.modelCode == owner.CONE3) ||
                      (foldOutObject.modelCode == owner.CONE4)
                     )
                    )
                addTreeConeNode(foldOutTreeRoot);            
            else
            {
                Vector thisLevel = new Vector();
                thisLevel.addElement(foldOutTreeRoot);
                // this constructs the whole tree
                addTreeLevel(thisLevel);
            }

            
            findRotationComponents(foldOutTreeRoot);        
        
//        Vector nodes = new Vector();
//        enumTree(foldOutTreeRoot, nodes, 0);
/*        
System.out.println("nodes = " + nodes.size()); 
int aCnt = 0;
for (int j = 0; j < nodes.size(); j++)
{   FoldOutTreeNode fotn = (FoldOutTreeNode) nodes.elementAt(j);
    int index = foldOutObject.containsFacet(fotn.facet);

if (Math.abs(fotn.minAngle - Math.PI) < Vector3D.NZero)
{   aCnt++;
}
 
        
}        
System.out.println("aCnt = " + aCnt);        
*/

            // set inside invisible
            for (int p = 0; p < foldOutObjectGroup.numFacets; p++)
            {   Facet3D f = foldOutObjectGroup.facets[p];
                boolean include = true;
                if (foldOutObjectGroup instanceof ObjectWithLine)
                {   ObjectWithLine owl = (ObjectWithLine) foldOutObjectGroup;
                    include = owl.replacesOrigObject(f) && !owl.hasReplacement(f);
                }        
                else if (foldOutObjectGroup instanceof ObjectWithPlane)
                {   ObjectWithPlane owp = (ObjectWithPlane) foldOutObjectGroup;
                    include = owp.replacesOrigObject(f) && !owp.hasReplacement(f);
                }        
                f.visible = include;    
            } // for

            foldOut(foldOutTreeRoot, foldOutInit);
            panel3D.showInside = true;
            //foldOutObjectGroup.findDiameter();
            oldFilled = filled;
            oldPos = Matrix3D.copy(panel3D.mat);
            setFilled(true);
            panel3D.initializeModel(foldOutObjectGroup, false);
            
            
            //owner.rightToolBar.wireSolidButton.setImage(owner.wireFrameImage);
            owner.rightToolBar.wireSolidButton.setDown(true);

            setSlider(true, foldOutInit, 0, 1);
            
            owner.helpBar.setText(TextConstants.rotateText);
            
//GWT            
//          //owner.rightToolBar.conDrawButton.setImage(owner.figureImage);
            //owner.rightToolBar.conDrawButton.setDown(true);
            
            
        } // else if (stepNum == 1)
    }
    
    
    
/*    
// tijdelijk
public void enumTree(FoldOutTreeNode startNode, Vector nodes, int level)
{   nodes.addElement(startNode);
    startNode.level = level;
    for (int i = 0; i < startNode.childNodes.size(); i++)
    {    FoldOutTreeNode fotn = (FoldOutTreeNode) startNode.childNodes.elementAt(i); 
         enumTree(fotn, nodes, level+1);
    }
}    
*/

    // lastLevel contains the FoldTreeNodes constructed in the 
    // last pass
    public void addTreeLevel(Vector lastLevel)
    {   Vector thisLevel = new Vector();
        // for each node created in the last level
        for (int i = 0; i < lastLevel.size(); i++)
        {   FoldOutTreeNode ftn = (FoldOutTreeNode) lastLevel.elementAt(i);
            // get the facet of ftn
            Facet3D fa = ftn.facet;
            // walk along the edges of fa
            for (int j = 0; j < fa.numPoints; j++)
            {   Vector3D edgeStart = fa.points[j];
                Vector3D edgeEnd = fa.points[(j + 1) % fa.numPoints];
                // could be invisible
                Facet3D adjacent = 
                    foldOutObject.facetContaining(edgeEnd, edgeStart, true);
                // find the index
                int adjIndex = foldOutObject.containsFacet(adjacent);
                // facet not yet labeled
                if (!facetsUsed[adjIndex])
                {
                    // minimum angle
                    Vector3D fNormal = new Vector3D(fa.normal);
                    Vector3D.makeUnitary(fNormal);
                    Vector3D adjNormal = new Vector3D(adjacent.normal);
                    Vector3D.makeUnitary(adjNormal);
                    double cosAngle = Vector3D.dotProduct(
                        fNormal, adjNormal);
                    // round off    
                    if (cosAngle > 1)
                        cosAngle = 1;
                    if (cosAngle < -1)
                        cosAngle = -1;
                    double minAngle = Math.PI - Math.acos(cosAngle);    
/*                    
int aCnt = 0;
if (cosAngle == 1)
{   System.out.println("un = " + UF.format(fNormal.x, 3) +
                       " & = " + UF.format(fNormal.y, 3) +
                       " & = " + UF.format(fNormal.z, 3));
System.out.println("uadj = " + UF.format(adjNormal.x, 3) +
                       " & = " + UF.format(adjNormal.y, 3) +
                       " & = " + UF.format(adjNormal.z, 3));    
aCnt++;                       
    
} 

System.out.println("aCnt = " + aCnt);
*/
                    int axisFrom = Facet3D.containsVertex(adjacent, edgeStart);
                    int axisTo = Facet3D.containsVertex(adjacent, edgeEnd);
                    // rotation axis    
//                    Line3D rotAxis = new Line3D(edgeStart, edgeEnd);
                    // create a node for this facet
                    FoldOutTreeNode newNode = 
                        new FoldOutTreeNode(adjacent, minAngle, axisFrom, axisTo);
                    // fix tree structure
                    newNode.parentNode = ftn;
                    ftn.childNodes.addElement(newNode);
                    // add to this level
                    thisLevel.addElement(newNode);
                    // label facet
                    facetsUsed[adjIndex] = true;
                }
                // else do nothing
            }    
        }
        if (thisLevel.size() > 0)
            addTreeLevel(thisLevel);
        // else finished    
    }


    // depth tree construction algo
    public void addTreeNode(FoldOutTreeNode lastNode)
    {   
        // get the facet of lastNode
        Facet3D fa = lastNode.facet;
        // walk along the edges of fa
        for (int j = 0; j < fa.numPoints; j++)
        {   Vector3D edgeStart = fa.points[j];
            Vector3D edgeEnd = fa.points[(j + 1) % fa.numPoints];
            // could be invisible, but only 1
            Facet3D adjacent = 
                 foldOutObject.facetContaining(edgeEnd, edgeStart, true);
            // find the index
            int adjIndex = foldOutObject.containsFacet(adjacent);
            // facet not yet labeled
            if (!facetsUsed[adjIndex])
            {
                // minimum angle
                Vector3D fNormal = new Vector3D(fa.normal);
                Vector3D.makeUnitary(fNormal);
                Vector3D adjNormal = new Vector3D(adjacent.normal);
                Vector3D.makeUnitary(adjNormal);
                double cosAngle = Vector3D.dotProduct(
                    fNormal, adjNormal);
                // round off    
                if (cosAngle > 1)
                    cosAngle = 1;
                if (cosAngle < -1)
                    cosAngle = -1;
                double minAngle = Math.PI - Math.acos(cosAngle);    
/*                    
int aCnt = 0;
if (cosAngle == 1)
{   System.out.println("un = " + UF.format(fNormal.x, 3) +
                       " & = " + UF.format(fNormal.y, 3) +
                       " & = " + UF.format(fNormal.z, 3));
System.out.println("uadj = " + UF.format(adjNormal.x, 3) +
                       " & = " + UF.format(adjNormal.y, 3) +
                       " & = " + UF.format(adjNormal.z, 3));    
aCnt++;                       
    
} 

System.out.println("aCnt = " + aCnt);
*/
                int axisFrom = Facet3D.containsVertex(adjacent, edgeStart);
                int axisTo = Facet3D.containsVertex(adjacent, edgeEnd);
                // create a node for this facet
                FoldOutTreeNode newNode = 
                    new FoldOutTreeNode(adjacent, minAngle, axisFrom, axisTo);
                // fix tree structure
                newNode.parentNode = lastNode;
                lastNode.childNodes.addElement(newNode);
                // label facet
                facetsUsed[adjIndex] = true;
                addTreeNode(newNode);
            }
            // else do nothing
        }
    }


    // depth tree construction algo modified for CYLINDER
    // assume we start at a SIDE facet
    public void addTreeCylinderNode(FoldOutTreeNode startNode)
    {   Vector thisLevel = new Vector();
        thisLevel.addElement(startNode);      
        // get the facet of lastNode
        Facet3D fa = startNode.facet;
        // walk along the edges of fa
        for (int j = 0; j < fa.numPoints; j++)
        {   Vector3D edgeStart = fa.points[j];
            Vector3D edgeEnd = fa.points[(j + 1) % fa.numPoints];
            // could be invisible, but only 1
            Facet3D adjacent = 
                 foldOutObject.facetContaining(edgeEnd, edgeStart, true);
            // find the index
            int adjIndex = foldOutObject.containsFacet(adjacent);
            // facet not yet labeled and top or bottom
            if (!facetsUsed[adjIndex] && (adjacent.numPoints > 4))
            {
                // minimum angle
                Vector3D fNormal = new Vector3D(fa.normal);
                Vector3D.makeUnitary(fNormal);
                Vector3D adjNormal = new Vector3D(adjacent.normal);
                Vector3D.makeUnitary(adjNormal);
                double cosAngle = Vector3D.dotProduct(
                    fNormal, adjNormal);
                // round off    
                if (cosAngle > 1)
                    cosAngle = 1;
                if (cosAngle < -1)
                    cosAngle = -1;
                double minAngle = Math.PI - Math.acos(cosAngle);    
/*                    
int aCnt = 0;
if (cosAngle == 1)
{   System.out.println("un = " + UF.format(fNormal.x, 3) +
                       " & = " + UF.format(fNormal.y, 3) +
                       " & = " + UF.format(fNormal.z, 3));
System.out.println("uadj = " + UF.format(adjNormal.x, 3) +
                       " & = " + UF.format(adjNormal.y, 3) +
                       " & = " + UF.format(adjNormal.z, 3));    
aCnt++;                       
    
} 

System.out.println("aCnt = " + aCnt);
*/
                int axisFrom = Facet3D.containsVertex(adjacent, edgeStart);
                int axisTo = Facet3D.containsVertex(adjacent, edgeEnd);
                // create a node for this facet
                FoldOutTreeNode newNode = 
                    new FoldOutTreeNode(adjacent, minAngle, axisFrom, axisTo);
                // fix tree structure
                newNode.parentNode = startNode;
                startNode.childNodes.addElement(newNode);
                // label facet
                facetsUsed[adjIndex] = true;
            }
            // else do nothing
        } // for
        // now top and bottom have been added to the side
        addTreeLevel(thisLevel);
    }


    // depth tree construction algo modified for CONE
    // assume we start at a SIDE facet    
    public void addTreeConeNode(FoldOutTreeNode startNode)
    {   Vector thisLevel = new Vector();
        thisLevel.addElement(startNode);      
        // get the facet of lastNode
        Facet3D fa = startNode.facet;
        // walk along the edges of fa
        for (int j = 0; j < fa.numPoints; j++)
        {   Vector3D edgeStart = fa.points[j];
            Vector3D edgeEnd = fa.points[(j + 1) % fa.numPoints];
            // could be invisible, but only 1
            Facet3D adjacent = 
                 foldOutObject.facetContaining(edgeEnd, edgeStart, true);
            // find the index
            int adjIndex = foldOutObject.containsFacet(adjacent);
            // facet not yet labeled and bottom or top of cut-off
            if (!facetsUsed[adjIndex] && (adjacent.numPoints > 4))
            {
                // minimum angle
                Vector3D fNormal = new Vector3D(fa.normal);
                Vector3D.makeUnitary(fNormal);
                Vector3D adjNormal = new Vector3D(adjacent.normal);
                Vector3D.makeUnitary(adjNormal);
                double cosAngle = Vector3D.dotProduct(
                    fNormal, adjNormal);
                // round off    
                if (cosAngle > 1)
                    cosAngle = 1;
                if (cosAngle < -1)
                    cosAngle = -1;
                double minAngle = Math.PI - Math.acos(cosAngle);    
/*                    
int aCnt = 0;
if (cosAngle == 1)
{   System.out.println("un = " + UF.format(fNormal.x, 3) +
                       " & = " + UF.format(fNormal.y, 3) +
                       " & = " + UF.format(fNormal.z, 3));
System.out.println("uadj = " + UF.format(adjNormal.x, 3) +
                       " & = " + UF.format(adjNormal.y, 3) +
                       " & = " + UF.format(adjNormal.z, 3));    
aCnt++;                       
    
} 

System.out.println("aCnt = " + aCnt);
*/
                int axisFrom = Facet3D.containsVertex(adjacent, edgeStart);
                int axisTo = Facet3D.containsVertex(adjacent, edgeEnd);
                // create a node for this facet
                FoldOutTreeNode newNode = 
                    new FoldOutTreeNode(adjacent, minAngle, axisFrom, axisTo);
                // fix tree structure
                newNode.parentNode = startNode;
                startNode.childNodes.addElement(newNode);
                // label facet
                facetsUsed[adjIndex] = true;
                
            }
            // else do nothing
        } // for
        // now bottom has been added
        addTreeLevel(thisLevel);
    }

    // recursively
    public void findRotationComponents(FoldOutTreeNode startNode)
    {   if (startNode.parentNode != null)
        {
        Facet3D startFacet = startNode.facet;
        // find and add replacements of startFacet here   
        Vector replacements = new Vector();
        if (foldOutObjectGroup instanceof ObjectWithLine)
            replacements = ((ObjectWithLine) foldOutObjectGroup).getReplacements(startFacet);
        else if (foldOutObjectGroup instanceof ObjectWithPlane)
            replacements = ((ObjectWithPlane) foldOutObjectGroup).getReplacements(startFacet);    
        //else // unchanged dummy object group
// always add startFacet since this contains
// the axisdata
            replacements.addElement(startFacet);
        for (int m = 0; m < replacements.size(); m++)    
            startNode.foldOutFacets.addElement(replacements.elementAt(m));
        }
        for (int i = 0; i < startNode.childNodes.size(); i++)
        {   FoldOutTreeNode childNode = (FoldOutTreeNode) startNode.childNodes.elementAt(i);
            // this fixes the whole subtree!!
            findRotationComponents(childNode);
            if (startNode.parentNode != null)
            {
            for (int j = 0; j < childNode.foldOutFacets.size(); j++)
                startNode.foldOutFacets.addElement(
                    childNode.foldOutFacets.elementAt(j));
            }        
        }
    }
    
    // recursively
    // foldOutFactor bewteen 0 and 1
    public void foldOut(FoldOutTreeNode startNode, double foldOutFactor)
    {   // skip the root completely
        if (startNode.parentNode != null)
        {   // determine angle which is needed if no folding
            // occured yet, between minAngle and Math.PI
            // relative to minAngle
            double foldAngle = //startNode.minAngle +
                (Math.PI - startNode.minAngle)* foldOutFactor; 
            // adapt to current fold status    
            // note: currentAngle is also relative to minAngle
            double rotAngle = foldAngle - startNode.currentAngle;    
if (Math.abs(rotAngle) > Vector3D.NZero)
{
//System.out.println("sNode-level = " + startNode.level);
//System.out.println("cos = " + UF.format(Math.cos(rotAngle), 12));
//System.out.println("sNode-rotAngle = " + 
//                   UF.format(rotAngle * 360 / (2 * Math.PI), 12));
}                   
            startNode.currentAngle = foldAngle;
            // find rotation axis            
            Vector3D axisStart = startNode.facet.points[startNode.axisFrom];
            Vector3D axisEnd = startNode.facet.points[startNode.axisTo];
            Line3D rotationAxis = new Line3D(axisStart, axisEnd);
            for (int j = 0; j < startNode.foldOutFacets.size(); j++)
            {   Facet3D fa = (Facet3D) startNode.foldOutFacets.elementAt(j);
                // find the object containing fa
                Object3D ob = foldOutObjectGroup.objectContains(fa);
                for (int k = 0; k < fa.numPoints; k++)
                {   // find point k on facet 
                    Vector3D v = fa.points[k];
                    // rotate it around axis
                    Vector3D rotV = rotationAxis.rotateBy(v, rotAngle);
                    // replace vertex in vertex array
                    // deze vertex staat op index fa.indices[k]
                    // in ob.vertices
//System.out.println("ob.vertices = " + ob.vertices.length);                    
                    ob.vertices[fa.indices[k]] = rotV;
                    fa.points[k] = rotV;                
//                    fa.setNormal();
                }
                fa.setNormal();                
            }
        }
        
        for (int i = 0; i < startNode.childNodes.size(); i++)
        {   FoldOutTreeNode childNode = (FoldOutTreeNode) startNode.childNodes.elementAt(i);
            // this folds the whole subtree!!
            foldOut(childNode, foldOutFactor);
        }
        
    }
    
// optie hulppunten:
// via een rebuild, 3 modes possible!
    
    
// bij andere knoppen waar nodig kijken of mouseMode == INERT    
    
    // top tool bar
    // lines
    public void drawLine(int stepNum, boolean b)
    {    
        // pressing the button
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            if ((mouseMode != INERT) && 
                ((mouseMode != DRAWLINE) ||
                 (DoorzienGWT.version == DoorzienGWT.EPN))
               )  
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
            	
            	// deze NIET!!
                //owner.topToolBar.drawLineButton.setDown(false);
                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                
                owner.topToolBar.showCutButton.setDown(false);                
                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;
                foldOutObjectGroup = null;
                
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);
                
                cutObjectGroup = null;                
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }
                //originalObject = currentObjectGroup.leftMostLeaf();
                // slider weg                                
                setSlider(false, 0, 0, 0);
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;    
                showHelpPointDrop(false);                
                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // drawLine afzetten
            // met FI blijf je in tool
// hier moet je dus nog meer resetten voor FI?            
            if (!b)
            {   owner.topToolBar.drawLineButton.setEnabled(true);
                mouseMode = INERT;
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();                                        
                helpPoint = false;
                panel3D.initializeModel(currentObjectGroup, false);
                
                owner.helpBar.setText(TextConstants.rotateText);
//GWT                
//                owner.enableOptions(true);
                
                return;
            }
            mouseMode = DRAWLINE;
            DrawConstants.TICKSVISIBLE = true;            
            showHelpPointDrop(true);            
//GWT            
//            owner.topToolBar.drawLineButton.setPressed(true);
                
            // kopieer current naar temp en stop dit in Panel3D
            tempObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject = tempObjectGroup.leftMostLeaf();
            
            tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
            fillPlanes(planesFilled);
            panel3D.initializeModel(tempObjectGroup, false);        
            // reset
            pointsSelected = 0;
            point1= null;
            point2 = null;
            movedPoint = null;
            movedEdgeWithPoint = null;
            clickedPoint = null;
            clickedEdgeWithPoint = null;
            
            owner.helpBar.setText(TextConstants.linePoint1Text);
            
            // now wait for mouse action
        }
        else if (stepNum == 1)
        {   

            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
                
                
                // reset
                // tempObjectGroup equals currentObjectGroup                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // temporarily replace with point added
                tempObjectGroup2 = 
                    new ObjectWithPoint(tempObjectGroup2, movedPoint, DrawConstants.lineColorIndex);
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpPoint();
                helpPoint = false;
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   

                // reset
                // tempObjectGroup equals currentObjectGroup                                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with point added
                tempObjectGroup2 = 
                    new ObjectWithPoint(tempObjectGroup2, movedEdgeWithPoint, DrawConstants.lineColorIndex);
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpPoint();
                helpPoint = false;
                panel3D.setPreviewModel(tempObjectGroup2);
                
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("all null");                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpPoint(xMoved, yMoved,
                    //new Point(xMoved, yMoved),
                    DrawConstants.lineColor);
                    
                return;

            }    
            // if one of clickedPoint or clickedEdgeWithPoint
            // is not null, tempObjectGroup2 contains the
            // object wanted, but recreate the exact one
            else if (clickedPoint != null)
            {   
System.out.println("clickedPoint");            	
            	
            	// replace with point added

            	
                tempObjectGroup = 
                    new ObjectWithPoint(tempObjectGroup, clickedPoint, DrawConstants.lineColorIndex);
                    
                   
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);
                panel3D.previewModel = null;
                panel3D.initializeModel(tempObjectGroup, false);        
                
                pointsSelected = 1;
                // save selection, reference sufficient
                point1 = clickedPoint;
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;
                
                owner.helpBar.setText(TextConstants.linePoint2Text);                            
            }
            else if (clickedEdgeWithPoint != null)
            {   
System.out.println("clickedEdgeWithPoint");               
                // replace with point added
               
                tempObjectGroup = 
                    new ObjectWithPoint(tempObjectGroup, clickedEdgeWithPoint, DrawConstants.lineColorIndex);
                    
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);                
                panel3D.previewModel = null;                
                panel3D.initializeModel(tempObjectGroup, false);        
                pointsSelected = 1;

                // save selection, reference sufficient
                point1 = clickedEdgeWithPoint[2];
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;
                
                owner.helpBar.setText(TextConstants.linePoint2Text);                            
            }
            

            // now wait again for mouse action
        }    
        else if (stepNum == 2)
        {   
            
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {   // twice the same vertex
                if (Vector3D.equals(point1, movedPoint))
                {   movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // line will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {
//System.out.println("OWL contains");             
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
                
//System.out.println("MP not null & ps = " + pointsSelected);                
                // reset
// this keeps the first point thickened                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // temporarily replace with line added
// thicken second point via ObjectWithLine?                
                tempObjectGroup2 = 
                    new ObjectWithLine(tempObjectGroup2, 
                        point1, movedPoint, DrawConstants.lineColorIndex, 0);
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);                
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
                
                // line will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
//System.out.println("MEWP not null & ps = " + pointsSelected);                                
                // reset
// this keeps the first point thickened                                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with line added
// thicken second point via ObjectWithLine?                                
                tempObjectGroup2 = 
                    new ObjectWithLine(tempObjectGroup2, 
                        point1, movedEdgeWithPoint[2], DrawConstants.lineColorIndex, 0);
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpLine();        
                panel3D.setPreviewModel(tempObjectGroup2);
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("MP and MEWP null");                                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point1, xMoved, yMoved,
                    //new Point(xMoved, yMoved),
                    DrawConstants.lineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        //new Point(xMoved, yMoved),
                        DrawConstants.lineColor);
                }    
                return;

            }    
            // check here for type of point AND if this was not 
            // equal to the first
            else if (clickedPoint != null)
            {   // twice the same vertex
                if (Vector3D.equals(point1, clickedPoint))
                {   
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // line will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedPoint))
                   )
                {
//System.out.println("OWL contains");             
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedPoint))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action    
                }   
                point2 = clickedPoint;   
                // now the line can be added
//setStart();                
                currentObjectGroup = new ObjectWithLine(currentObjectGroup,
                    point1, point2, DrawConstants.lineColorIndex, DrawConstants.llFactor);
//showTime("creating OWL-v");                    
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                fillPlanes(planesFilled);                            
                panel3D.initializeModel(currentObjectGroup, false);        
                
                
                
            }
            else if (clickedEdgeWithPoint != null)
            {   
                // line will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action   
                }   
                point2 = clickedEdgeWithPoint[2];   
                // now the line can be added
                
                currentObjectGroup = new ObjectWithLine(currentObjectGroup,
                    point1, point2, DrawConstants.lineColorIndex, DrawConstants.llFactor);
                   
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                fillPlanes(planesFilled);                            
                DrawConstants.TICKSVISIBLE = false;
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);        
            }    
            addToHistory();
            
            owner.helpBar.setText(TextConstants.rotateText);
            
            tempObjectGroup = null;
            tempObjectGroup2 = null;            
            //fillPlanes(planesFilled);            
            
            //mouseMode = INERT;
            setNumLines(numLines + 1);
            if (showCut)
                updateCutPanel();
            // for FI: stay in tool
            if (DoorzienGWT.version == DoorzienGWT.FI)
                drawLine(0, true);
            else if (DoorzienGWT.version == DoorzienGWT.EPN)
            {   mouseMode = INERT;
//GWT            
//                owner.enableOptions(true);
                owner.topToolBar.drawLineButton.setDown(false);
            }
        }
        
    }    
    
    public void deleteLine(int stepNum, boolean b)
    {   // als meer lijnen kies een lijn
        // gebruik mousemode
        if (stepNum == 0)
        {   // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
            	
                owner.topToolBar.drawLineButton.setDown(false);
                
                // deze NIET!!
                //owner.topToolBar.deleteLineButton.setDown(false);
                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;                
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }

                // slider weg                                
                setSlider(false, 0, 0, 0);
                //originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // deleteLine afzetten
            if (!b)
            {   mouseMode = INERT;
            
            	owner.helpBar.setText(TextConstants.rotateText);
//GWT            
//                owner.enableOptions(true);                
                return;
            }

            mouseMode = DELETELINE;
//GWT            
//            owner.enableOptions(false);
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
//System.out.println("co-size = " + construction.length);                        
            if (numLines > 1)
            {   // get the line via the mouse, check with construction
            	  owner.helpBar.setText(TextConstants.selectDeleteLineText);
//GWT            	
//                owner.topToolBar.deleteLineButton.setPressed(true);
                lineChoosen = null;
                // wait for mouse action
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.lineColor;
                
            }    
            else if (numLines == 1)
            {   // only one line, delete directly
                // find the unique line here
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Line3D)
                        lineChoosen = (Line3D) ob;
                    DrawConstants.llFactor = 0;    
                        
                }
                deleteLine(1, true);
            }    
        }
        else if (stepNum == 1)
        {
        	
            owner.topToolBar.deleteLineButton.setDown(false);
            helpPoint = false;
            panel3D.hideHelpPoint();
            
//System.out.println("lineChoosen = " + lineChoosen.toString());        
            construction.removeElement(lineChoosen);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();
            
            fillPlanes(planesFilled);            
            panel3D.initializeModel(currentObjectGroup, false);        
            addToHistory();
            //fillPlanes(planesFilled);
            // originalObject opnieuw!
            mouseMode = INERT;
            
            owner.helpBar.setText(TextConstants.rotateText);
            
            tempObjectGroup = null;
            setNumLines(numLines - 1);        
            if (showCut)
                updateCutPanel();
//GWT            
//            owner.enableOptions(true);
        }
    }
    
    public void lengthenLines()
    {   
// uitzondering voor rotate translate        
        if ((mouseMode != INERT) && (mouseMode != TRANSLATEPLANE) &&
            (mouseMode != ROTATEPLANE)
            )
        {   // zet model gelijk aan current
            // dit aborteert andere LOPENDE muis acties
        	
            owner.topToolBar.drawLineButton.setDown(false);                
            owner.topToolBar.deleteLineButton.setDown(false);                                
            owner.topToolBar.drawPlaneButton.setDown(false);                                
            owner.topToolBar.parPlaneButton.setDown(false);
            owner.topToolBar.deletePlaneButton.setDown(false);
            owner.topToolBar.showCutButton.setDown(false);                
            owner.topToolBar.cutButton.setDown(false);                                
            owner.rightToolBar.conDrawButton.setDown(false);
            
            tempObjectGroup = null;
            tempObjectGroup2 = null;            
            foldOutObjectGroup = null;
//GWT            
//            owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                            
            cutObjectGroup = null;                
            if (numPlanes > 0)
            {   
//GWT            	
//            	owner.topToolBar.cutButton.setImage(owner.cutImage);

            }
            //originalObject = currentObjectGroup.leftMostLeaf();
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                    
            panel3D.hideHelpPoint();
            helpPoint = false;
            DrawConstants.TICKSVISIBLE = false;            
            showHelpPointDrop(false);                            
            panel3D.initializeModel(currentObjectGroup, false);                    
            mouseMode = INERT;
        }
        // doe dit via een rebuild
        // omdat je dan ook nieuwe relevante snijpunten kan zien    
        // using llFactor in the constructors!
        // stapsgewijs
        if (currentObjectGroup instanceof ObjectWithLine)
           construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
        else if (currentObjectGroup instanceof ObjectWithPlane)
           construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
        // doe dit via een rebuild
        // omdat je dan ook nieuwe relevante snijpunten kan zien    
        // using llFactor in the constructors!
        double temp = DrawConstants.llFactor + LLSTEP;
        if (temp <= (MAXLLFACTOR + LLSTEP / 10))
        	DrawConstants.llFactor = temp;
        // rebuild
        currentObjectGroup = rebuild(originalObject, construction, null);
        originalObject = currentObjectGroup.leftMostLeaf();
        fillPlanes(planesFilled);
        panel3D.initializeModel(currentObjectGroup, false);        

        owner.helpBar.setText(TextConstants.rotateText);
        
       if (showCut)
           updateCutPanel();
//GWT        
//        owner.enableOptions(true);
    }    

    public void shortenLines()
    {   
// uitzondering voor rotate translate        
        if ((mouseMode != INERT) && (mouseMode != TRANSLATEPLANE) &&
            (mouseMode != ROTATEPLANE)
            )

        {   // zet model gelijk aan current
            // dit aborteert andere LOPENDE muis acties

        	owner.topToolBar.drawLineButton.setDown(false);                
            owner.topToolBar.deleteLineButton.setDown(false);                                
            owner.topToolBar.drawPlaneButton.setDown(false);                                
            owner.topToolBar.parPlaneButton.setDown(false);
            owner.topToolBar.deletePlaneButton.setDown(false);
            owner.topToolBar.showCutButton.setDown(false);                
            owner.topToolBar.cutButton.setDown(false);                                
            owner.rightToolBar.conDrawButton.setDown(false);                                                
            
        	
        	tempObjectGroup = null;
            tempObjectGroup2 = null;            
            foldOutObjectGroup = null;
//GWT            
//             owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                            
            cutObjectGroup = null;                
            if (numPlanes > 0)
            {   
//GWT            	
//            	owner.topToolBar.cutButton.setImage(owner.cutImage);

            }

            //originalObject = currentObjectGroup.leftMostLeaf();
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                    
            panel3D.hideHelpPoint();
            helpPoint = false;
            DrawConstants.TICKSVISIBLE = false;            
            showHelpPointDrop(false);                            
            panel3D.initializeModel(currentObjectGroup, false);                    
            mouseMode = INERT;            
        }
        if (currentObjectGroup instanceof ObjectWithLine)
           construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
        else if (currentObjectGroup instanceof ObjectWithPlane)
           construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
        // doe dit via een rebuild
        // omdat je dan ook nieuwe relevante snijpunten kan zien    
        // using llFactor in the constructors!
        DrawConstants.llFactor = 0;
        // rebuild
        currentObjectGroup = rebuild(originalObject, construction, null);
        originalObject = currentObjectGroup.leftMostLeaf();
        fillPlanes(planesFilled);
        panel3D.initializeModel(currentObjectGroup, false);        

        owner.helpBar.setText(TextConstants.rotateText);
        if (showCut)
            updateCutPanel();
//GWT        
//        owner.enableOptions(true);
        
        
        // originalObject opnieuw!
    }    
    
    // top tool bar
    // drawing planes
    public void drawPlane(int stepNum, boolean b)
    {    
    	
//System.out.println("draw plane " + stepNum + " " + b);

        // no points choosen
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if ((mouseMode != INERT) && 
                ((mouseMode != DRAWPLANE) || 
                 (DoorzienGWT.version == DoorzienGWT.EPN))
                ) 
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
            	
            	owner.topToolBar.drawLineButton.setDown(false);
            	
//System.out.println("mm = " + mouseMode);            
//System.out.println("dlb is down mm = " + owner.topToolBar.drawLineButton.isDown());
            	                
                owner.topToolBar.deleteLineButton.setDown(false);
                
                // deze NIET!!
                //owner.topToolBar.drawPlaneButton.setDown(false); 
                
//System.out.println("dlb is down mm = " + owner.topToolBar.drawLineButton.isDown());
                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }

                // slider weg                                
                setSlider(false, 0, 0, 0);
                
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                
                showHelpPointDrop(false);                                
                //originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // drawPlane afzetten
            // met FI blijf je in tool, dus alles nodig
            if (!b)
            {   
            	//owner.topToolBar.drawLineButton.setDown(false);
            	
            	//owner.topToolBar.drawPlaneButton.setEnabled(true);
            	
                mouseMode = INERT;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                panel3D.initializeModel(currentObjectGroup, false);
               
                owner.helpBar.setText(TextConstants.rotateText);
//GWT                
//                owner.enableOptions(true);                
                
//System.out.println("dlb is down !b = " + owner.topToolBar.drawLineButton.isDown());                
                return;
            }
            
            mouseMode = DRAWPLANE;
            DrawConstants.TICKSVISIBLE = true;  
            showHelpPointDrop(true);
//GWT            
//                owner.topToolBar.drawPlaneButton.setPressed(true);            
            // kopieer current naar temp en stop dit in Panel3D
//System.out.println("making deep copy");            
            tempObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject = tempObjectGroup.leftMostLeaf();
            
            tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
            
            panel3D.initializeModel(tempObjectGroup, false);        
            // reset
            pointsSelected = 0;
            point1 = null;
            point2 = null;
            point3 = null;
            
            movedPoint = null;
            movedEdgeWithPoint = null;
            clickedPoint = null;
            clickedEdgeWithPoint = null;
            
            owner.helpBar.setText(TextConstants.planePoint1Text);
            // now wait for mouse action
        }
        // one point indicated/choosen, process this
        else if (stepNum == 1)
        {   
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
//System.out.println("MP not null & ps = " + pointsSelected);                
                // reset
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // temporarily replace with point added
                tempObjectGroup2 = 
                    new ObjectWithPoint(tempObjectGroup2, movedPoint, 
                    		DrawConstants.planeOutlineColorIndex);
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpPoint();
                helpPoint = false;

                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
//System.out.println("MEWP not null & ps = " + pointsSelected);                                
                // reset
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with point added
                tempObjectGroup2 = 
                    new ObjectWithPoint(tempObjectGroup2, movedEdgeWithPoint, 
                    		DrawConstants.planeOutlineColorIndex);
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);                                
                panel3D.hideHelpPoint();
                helpPoint = false;

                panel3D.setPreviewModel(tempObjectGroup2);
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("MP and MEWP null");                                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpPoint(xMoved, yMoved, //new Point(xMoved, yMoved),
                    DrawConstants.planeOutlineColor);
                    
                return;

            }    
            // if one of clickedPoint or clickedEdgeWithPoint
            // is not null 
            else if (clickedPoint != null)
            {   // replace with point added
                tempObjectGroup = 
                    new ObjectWithPoint(tempObjectGroup, clickedPoint, DrawConstants.planeOutlineColorIndex);
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);                
                panel3D.previewModel = null;                                
                panel3D.initializeModel(tempObjectGroup, false);        
                pointsSelected = 1;
                // save selection, reference sufficient
                point1 = clickedPoint;
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                
                clickedPoint = null;
                clickedEdgeWithPoint = null;
            }
            else if (clickedEdgeWithPoint != null)
            {   // replace with point added
                tempObjectGroup = 
                    new ObjectWithPoint(tempObjectGroup, clickedEdgeWithPoint, DrawConstants.planeOutlineColorIndex);
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                fillPlanes(planesFilled);                
                panel3D.previewModel = null;                                
                panel3D.initializeModel(tempObjectGroup, false);        
                pointsSelected = 1;
                // save selection, reference sufficient
                point1 = clickedEdgeWithPoint[2];
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                
                clickedPoint = null;
                clickedEdgeWithPoint = null;
            }
            
            owner.helpBar.setText(TextConstants.planePoint2Text);            
            // now wait again for mouse action
        }    
        // two points choosen, process these
        else if (stepNum == 2)
        {   
            
            
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
//System.out.println("MP not null & ps = " + pointsSelected);                
                // reset
//                tempObjectGroup2 = (ObjectGroup3D) tempObjectGroup.deepCopy();
//                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();

                // twice the same vertex
                if (Vector3D.equals(point1, movedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                boolean hasLine = false;
                // line could be in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {   hasLine = true;
//System.out.println("line in OWL");                
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {   hasLine = true;
//System.out.println("line in OWP");                                
                }   
                if (hasLine)                
                {   // tempObjectGroup is ObjectWithPoint
                    tempObjectGroup2 = (ObjectGroup3D) tempObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                    
                    // replace with second point added
                    tempObjectGroup2 = 
                        new ObjectWithPoint(tempObjectGroup2, movedPoint, 
                        		DrawConstants.planeOutlineColorIndex);
//System.out.println("second point");                                                            
                }
                else
                {
                    // add line to copy of original
                    tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
// reset to current, thicken second point through ObjectWithLine?
                    tempObjectGroup2 = 
                        new ObjectWithLine(tempObjectGroup2, point1, movedPoint, 
                        		DrawConstants.planeOutlineColorIndex, 0);
//System.out.println("line added");                                                                                        
                }    
                fillPlanes(planesFilled);                  
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
//System.out.println("MEWP not null & ps = " + pointsSelected);                                
                // reset
//                tempObjectGroup2 = (ObjectGroup3D) tempObjectGroup.deepCopy();
//                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();

// note: if the first point was a point on an edge
// this is now a vertex
// at free drawing we could "hit" an earlier line??
                boolean hasLine = false;
/*                
                // line could be in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {   
                    hasLine = true;
                
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {   hasLine = true;
                }   
*/
                if (hasLine)                
                {   
                    
// dit zorgt voor stack overflow error
// vermoedelijk omdat je het nieuwe edge
// point te dicht bij een oude vertex 
// gekozen hebt?
                    // tempObjectGroup is ObjectWithPoint
                    tempObjectGroup2 = (ObjectGroup3D) tempObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                    
                    // replace with second point added
                    tempObjectGroup2 = 
                        new ObjectWithPoint(tempObjectGroup2, 
                            movedEdgeWithPoint[2], 
                            DrawConstants.planeOutlineColorIndex);
                }
                else
                {
// twee keer dezelfde lijn mag hier wel??
// tijdelijk??
                    
                    tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
// reset to current, thicken second point through ObjectWithLine?
// nee, kleur is niet goed?
                    tempObjectGroup2 = 
                        new ObjectWithLine(tempObjectGroup2, point1, 
                            movedEdgeWithPoint[2], 
                            DrawConstants.planeOutlineColorIndex, 0);
//System.out.println("line added");                                                                                                                    
                            
                }    
                fillPlanes(planesFilled);                                
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);
                
                
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("MP and MEWP null");                                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point1,xMoved, yMoved,
                    //new Point(xMoved, yMoved),
                    DrawConstants.planeOutlineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        //new Point(xMoved, yMoved),
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            
            
            // check here for type of point AND if this was not 
            // equal to the first
// of: twee keer zelfde edge selecteerd edge als lijn!
// d.i. consistent met Doorzien3
            else if (clickedPoint != null)
            {   
                // twice the same vertex
                if (Vector3D.equals(point1, clickedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                boolean hasLine = false;
                // line could be in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedPoint))
                   )
                {   hasLine = true;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedPoint))
                   )
                {   hasLine = true;
                }   
                if (hasLine)                
                {   // replace with second point added

                    tempObjectGroup = 
                        new ObjectWithPoint(tempObjectGroup, clickedPoint, 
                        		DrawConstants.planeOutlineColorIndex);
                }
                else
                {
                    tempObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject = tempObjectGroup.leftMostLeaf();
// reset to current, thicken second point through ObjectWithLine?
                    tempObjectGroup = 
                        new ObjectWithLine(tempObjectGroup, point1, clickedPoint, 
                        		DrawConstants.planeOutlineColorIndex, 0);
                }    
                fillPlanes(planesFilled);                
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.initializeModel(tempObjectGroup, false);        
                pointsSelected = 2;
                // save selection, reference sufficient
                point2 = clickedPoint;
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;                    
            }
            else if (clickedEdgeWithPoint != null)
            {
// note: if the first point was a point on an edge
// this is now a vertex
                boolean hasLine = false;
/*                
                // line could be in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedEdgeWithPoint[2]))
                   )
                {   hasLine = true;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedEdgeWithPoint[2]))
                   )
                {   hasLine = true;
                }   
*/
// zie boven, stack overflow error
                if (hasLine)                
                {   // replace with second point added
                    tempObjectGroup = 
                        new ObjectWithPoint(tempObjectGroup, clickedEdgeWithPoint[2], 
                        		DrawConstants.planeOutlineColorIndex);
                }
                else
                {
                    tempObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject = tempObjectGroup.leftMostLeaf();
// reset to current, thicken second point through ObjectWithLine?
// nee, kleur is niet goed?
                    tempObjectGroup = 
                        new ObjectWithLine(tempObjectGroup, point1, clickedEdgeWithPoint[2], 
                        		DrawConstants.planeOutlineColorIndex, 0);
                }    
                fillPlanes(planesFilled);                
                // remains unchanged                       
                // originalObject = tempObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.initializeModel(tempObjectGroup, false);        
                pointsSelected = 2;
                // save selection, reference sufficient
                point2 = clickedEdgeWithPoint[2];
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;
// hier even geen objectWithLine maken: 
// je kan de lijn al hebben, die zou je dan rood moeten kleuren

// maak de lijn als je hem nog niet hebt
            }            
            
            owner.helpBar.setText(TextConstants.planePoint3Text);            
        }
        // third point choosen
        else if (stepNum == 3)
        {   
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
                // twice the same vertex
                if (Vector3D.equals(point1, movedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                if (Vector3D.equals(point2, movedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                // 3 collinear points    
                //if ((point1 != null) && (point2 != null) &&
                if (Line3D.areCollinear(point1, point2, movedPoint))
                {   // message?
//System.out.println("collinear");                
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedPoint))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedPoint))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
//System.out.println("MP not null & ps = " + pointsSelected);                
                // reset
// add to current, highlighting?                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
  
                
                // temporarily replace with plane added
                tempObjectGroup2 = 
                    new ObjectWithPlane(tempObjectGroup2, 
                        point1, point2, movedPoint, 
                        DrawConstants.planeOutlineColorIndex, true);
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);      
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
//System.out.println("MEWP not null & ps = " + pointsSelected);                                

                // 3 collinear points    
                if (Line3D.areCollinear(point1, point2, movedEdgeWithPoint[2]))                
                {   // message?
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWP contains");   
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   

                // reset
// take current, highlighting?                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with plane added
                tempObjectGroup2 = 
                    new ObjectWithPlane(tempObjectGroup2, 
                        point1, point2, movedEdgeWithPoint[2], 
                        DrawConstants.planeOutlineColorIndex, true);
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);      
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("MP and MEWP null");                                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point2,xMoved, yMoved,
                    //new Point(xMoved, yMoved),
                    DrawConstants.planeOutlineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        //new Point(xMoved, yMoved),
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            // vertex choosen    
            else if (clickedPoint != null)
            {   
                
                // twice the same vertex
                if (Vector3D.equals(point1, clickedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                if (Vector3D.equals(point2, clickedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                // 3 collinear points    
                //if ((point1 != null) && (point2 != null) &&
                if (Line3D.areCollinear(point1, point2, clickedPoint))
                {   // message?
//System.out.println("collinear");                
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedPoint))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedPoint))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;    
                }   
                point3 = clickedPoint;   
                // now the plane can be added
                currentObjectGroup = new ObjectWithPlane(currentObjectGroup,
                    point1, point2, point3, DrawConstants.planeOutlineColorIndex, true);
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                                
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);        
                   
            }
            // point on edge choosen
            else if (clickedEdgeWithPoint != null)
            {
// note: if the first or second point was a point on an edge
// these are now a vertices
                // 3 collinear points    
                if (Line3D.areCollinear(point1, point2, clickedEdgeWithPoint[2]))                
                {   // message?
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedEdgeWithPoint[2]))
                   )
                {
//System.out.println("OWP contains");   
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                point3 = clickedEdgeWithPoint[2];   
                // now the plane can be added
                currentObjectGroup = new ObjectWithPlane(currentObjectGroup,
                    point1, point2, point3, DrawConstants.planeOutlineColorIndex, true);
//System.out.println("plane added");                    
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);        

            }
            addToHistory();
            // aan het einde
            
            owner.helpBar.setText(TextConstants.rotateText);
            
            tempObjectGroup = null;            
            tempObjectGroup2 = null;                        
//            fillPlanes(planesFilled);
            setNumPlanes(numPlanes + 1);
            if (showCut)
                updateCutPanel();
            // for FI: stay in tool
            if (DoorzienGWT.version == DoorzienGWT.FI)
                drawPlane(0, true);                
            // for EPN out of tool    
            else if (DoorzienGWT.version == DoorzienGWT.EPN)
            {   mouseMode = INERT;
//GWT            
//                owner.enableOptions(true);
                owner.topToolBar.drawPlaneButton.setDown(false);                            
            }
        }
        
// check voor collinear en of je het vlak al hebt                
    } // drawPlane    


    public void drawParPlane(int stepNum, boolean b)
    {   // als meer vlakken kies een vlak
        // gebruik mousemode
        boolean wasReplaced = false;
        
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
            	// dit aborteert andere LOPENDE muis acties
            	
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);
                
                // deze NIET!!
                //owner.topToolBar.parPlaneButton.setDown(false);
                
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
            	
            	
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }
                
                // slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // drawParPlane afzetten
            // later met FI je in tool? dan meer toevoegen
            // zie drawPlane
            if (!b)
            {   
                mouseMode = INERT;
                
                owner.helpBar.setText(TextConstants.rotateText);                
//GWT                
//                owner.enableOptions(true);                
                return;
            }
            
            mouseMode = DRAWPARPLANE;
//GWT
//            owner.topToolBar.parPlaneButton.setPressed(true);                                            
            DrawConstants.TICKSVISIBLE = true;        
            showHelpPointDrop(true);                
            // construction wiil always be created since there 
            // is at least one plane
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
//System.out.println("co-size = " + construction.size());                        
            if (numPlanes > 1)
            {   Plane3D firstPlane = null;
                boolean allParallel = true;
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Plane3D)
                    {    if (firstPlane == null)
                             firstPlane = ((Plane3D) ob).copy();
                         else
                         {    Plane3D aPlane = ((Plane3D) ob).copy();
                              int isType = Plane3D.intersectionType(firstPlane, aPlane);
                              allParallel = allParallel && (isType != 1);
                         }   
                    }
                } // for              
                // take firstPlane
                if (allParallel)                    
                {   parPlaneChoosen = firstPlane;
                    parPointChoosen = null;
                    helpPoint = true;
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    
                    owner.helpBar.setText(TextConstants.parPlanePointText);                                
                    // wait for mouse action
                    // choosing the point

                }
                else
                {
                    // get the plane via the mouse
              	
                    owner.helpBar.setText(TextConstants.selectParPlaneText);                                        
                    parPlaneChoosen = null;
                    parPointChoosen = null;
                    helpPoint = true;
                    panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                    // wait for mouse action
                }
            }    
            else // numPlanes = 1
            {   // only one plane, choose directly
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Plane3D)
                        parPlaneChoosen = ((Plane3D) ob).copy();
                }
                parPointChoosen = null;
                helpPoint = true;
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;
                
                owner.helpBar.setText(TextConstants.parPlanePointText);                            
                // wait for mouse action
                // choosing the point

            }
        }
        else if (stepNum == 1)
        {   //owner.helpBar.setText(owner.tt("parPlanePointText"));            
            // for preview
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(movedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, movedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, movedPoint)))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(movedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, movedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, movedPoint)))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
//System.out.println("MP not null & ps = " + pointsSelected);                
                // reset
// add to current, highlighting?                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
  
                
                // temporarily replace with plane added
                tempObjectGroup2 = 
                    new ObjectWithPlane(tempObjectGroup2, movedPoint,
                        Vector3D.plus(parPlaneChoosen.direction1, movedPoint),
                        Vector3D.plus(parPlaneChoosen.direction2, movedPoint),
                        DrawConstants.planeOutlineColorIndex, true);
                // remains unchanged                       
                // original2Object = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);      
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
//System.out.println("MEWP not null & ps = " + pointsSelected);                                

                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(movedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, movedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, movedEdgeWithPoint[2])))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(movedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, movedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, movedEdgeWithPoint[2])))
                   )
                {
//System.out.println("OWP contains");   
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   

                // reset
// take current, highlighting?                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with plane added
                tempObjectGroup2 = 
                    new ObjectWithPlane(tempObjectGroup2, movedEdgeWithPoint[2],
                        Vector3D.plus(parPlaneChoosen.direction1, movedEdgeWithPoint[2]),
                        Vector3D.plus(parPlaneChoosen.direction2, movedEdgeWithPoint[2]),
                        DrawConstants.planeOutlineColorIndex, true);
                // remains unchanged                       
                // originalObject2 = tempObjectGroup2.leftMostLeaf();
                fillPlanes(planesFilled);      
                panel3D.hideHelpLine();                        
                panel3D.setPreviewModel(tempObjectGroup2);
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved is != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
//System.out.println("MP and MEWP null");                                
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
//                panel3D.showHelpLine(point2,
//                    new Point(xMoved, yMoved),
//                    planeOutlineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        //new Point(xMoved, yMoved),
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            // vertex choosen    
            else if (clickedPoint != null)
            {   
                
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(clickedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, clickedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedPoint)))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(clickedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, clickedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedPoint)))
                   )
                {
//System.out.println("OWP contains");                                                        
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;    
                }   
                parPointChoosen = new Vector3D(clickedPoint);   
                // now the plane can be added
/*                
System.out.println("going to create plane");                
Plane3D test = new Plane3D(parPointChoosen,
                    Vector3D.plus(parPlaneChoosen.direction1, parPointChoosen),
                    Vector3D.plus(parPlaneChoosen.direction2, parPointChoosen));
System.out.println("test = " + test.toString());                    
*/
// om een of ander reden is soms de "structuur" van
// currentObjectGroup hier niet goed
// wel na een deep copy

// er wordt in de snijalgorithme een facet niet doorgesneden
// dit is altijd een facet van origObject
// maar niet omgekeerd!
// de cut bevat WEL voldoende vertices maar kan daardoor
// NIET gemaakt worden

// het niet doorgesneden facet heeft GEEN replacement maar wordt
// incorrect geclassificeerd als liggend "left" van het vlak
// i.p.v. "to be cut"
// dit verdwijnt na de deep copy
// dus: de coordinaten zijn niet goed
// maar waarom werkt alles dan in drawPlane wel OK?

                currentObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
                originalObject = currentObjectGroup.leftMostLeaf();

                currentObjectGroup = new ObjectWithPlane(currentObjectGroup,
                    parPointChoosen,
                    Vector3D.plus(parPlaneChoosen.direction1, parPointChoosen),
                    Vector3D.plus(parPlaneChoosen.direction2, parPointChoosen),
                    DrawConstants.planeOutlineColorIndex, true);
                    
                    
                    
                    
wasReplaced = false;
if (currentObjectGroup.objects.size() > 1)
    wasReplaced = true;
//System.out.println("wasReplaced = " + wasReplaced);

                    
//System.out.println("plane created");                                    
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                                
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);        
                   
            }
            // point on edge choosen
            else if (clickedEdgeWithPoint != null)
            {
// note: if the first or second point was a point on an edge
// these are now a vertices
                // plane will be added to currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(clickedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, clickedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedEdgeWithPoint[2])))
                   )
                {
//System.out.println("OWL contains");                                    
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(clickedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, clickedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedEdgeWithPoint[2])))
                   )
                {
//System.out.println("OWP contains");   
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                parPointChoosen = new Vector3D(clickedEdgeWithPoint[2]);   
                
// om een of ander reden is de "structuur" van
// currentObjectGroup hier niet goed
// wel na een kopie
                currentObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();                
                originalObject = currentObjectGroup.leftMostLeaf();
                
                // now the plane can be added
                currentObjectGroup = new ObjectWithPlane(currentObjectGroup,
                    parPointChoosen,
                    Vector3D.plus(parPlaneChoosen.direction1, parPointChoosen),
                    Vector3D.plus(parPlaneChoosen.direction2, parPointChoosen),
                    DrawConstants.planeOutlineColorIndex, true);
//System.out.println("plane added");                    


wasReplaced = false;
if (currentObjectGroup.objects.size() > 1)
    wasReplaced = true;
//System.out.println("wasReplaced = " + wasReplaced);

                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                helpPoint = false;
                fillPlanes(planesFilled);
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);        

            }
            // dit alleen als er een nieuw vlak
            // bijgekomen is
            if (wasReplaced)
            {
                setNumPlanes(numPlanes + 1);
                if (showCut)
                    updateCutPanel();
                addToHistory();
            }
            // dit altijd
            // aan het einde 
            
//            owner.helpBar.setText(Table.lookUp("rotateText"));
            owner.helpBar.setText(TextConstants.rotateText);
            
            tempObjectGroup = null;            
            tempObjectGroup2 = null;                        
            mouseMode = INERT;
//GWT            
//            owner.enableOptions(true);
            owner.topToolBar.parPlaneButton.setDown(false);                                            
        }
    }    


    public void deletePlane(int stepNum, boolean b)
    {   // als meer vlakken kies een vlak
        // gebruik mousemode
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                
                //deze NIET!!
                //owner.topToolBar.deletePlaneButton.setDown(false);
                
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
            	
            	
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }
//fout?                
//                    owner.topToolBar.cutButton.setImage(owner.cutImage);
                
                // slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            if (!b)
            {   mouseMode = INERT;
            
            	owner.helpBar.setText(TextConstants.rotateText);
//GWT            
//                owner.enableOptions(true);                
                return;
            }
            
            mouseMode = DELETEPLANE;
//GWT            
//            owner.enableOptions(false);
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
//System.out.println("co-size = " + construction.length);                        
            if (numPlanes > 1)
            {   // get the plane via the mouse
                owner.helpBar.setText(TextConstants.selectDeletePlaneText);            	

//GWT            	
//                owner.topToolBar.deletePlaneButton.setPressed(true);
                planeChoosen = null;
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                
            }    
            else
            {   // only one plane, delete directly
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Plane3D)
                        planeChoosen = ((Plane3D) ob).copy();
                }
                deletePlane(1, true);
            }
        }
        else if (stepNum == 1)
        {
        	
            owner.topToolBar.deletePlaneButton.setDown(false);
            helpPoint = false;
            panel3D.hideHelpPoint();
            
            // rebuild
            construction.removeElement(planeChoosen);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();
            fillPlanes(planesFilled);            
            panel3D.initializeModel(currentObjectGroup, false);        
            addToHistory();
            //fillPlanes(planesFilled);
            // originalObject opnieuw!
            mouseMode = INERT;
            
            owner.helpBar.setText(TextConstants.rotateText);
            
            setNumPlanes(numPlanes - 1);        
            if (showCut)
            {   if (planeChoosen.equals(cutPlane))
                    killCutPanel();
                else
                    updateCutPanel();
                
            }
//GWT            
//            owner.enableOptions(true);
        }
    }    

    public void fillPlanes(boolean b)
    {   planesFilled = b;
        // this sets the flaggs
        if (currentObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) currentObjectGroup).fillCuts(b);
        else if (currentObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) currentObjectGroup).fillCuts(b);
        else if (currentObjectGroup instanceof ObjectWithPoint)
            ((ObjectWithPoint) currentObjectGroup).fillCuts(b);
            
        if (tempObjectGroup != null)
        {
            if (tempObjectGroup instanceof ObjectWithPlane)
                ((ObjectWithPlane) tempObjectGroup).fillCuts(b);
            else if (tempObjectGroup instanceof ObjectWithLine)
                ((ObjectWithLine) tempObjectGroup).fillCuts(b);    
            else if (tempObjectGroup instanceof ObjectWithPoint)
                ((ObjectWithPoint) tempObjectGroup).fillCuts(b);    
                
        }    
        if (tempObjectGroup2 != null)
        {
            if (tempObjectGroup2 instanceof ObjectWithPlane)
                ((ObjectWithPlane) tempObjectGroup2).fillCuts(b);
            else if (tempObjectGroup2 instanceof ObjectWithLine)
                ((ObjectWithLine) tempObjectGroup2).fillCuts(b);    
            else if (tempObjectGroup2 instanceof ObjectWithPoint)
                ((ObjectWithPoint) tempObjectGroup2).fillCuts(b);    
                
        }    
        
        if (cutObjectGroup != null)
            {   ObjectGroup3D left = (ObjectGroup3D) cutObjectGroup.objects.elementAt(0);
                ObjectGroup3D right = (ObjectGroup3D) cutObjectGroup.objects.elementAt(1);
            // this sets the flaggs
            if (left instanceof ObjectWithPlane)
                ((ObjectWithPlane) left).fillCuts(b);
            else if (left instanceof ObjectWithLine)
                ((ObjectWithLine) left).fillCuts(b);    
            if (right instanceof ObjectWithPlane)
                ((ObjectWithPlane) right).fillCuts(b);
            else if (right instanceof ObjectWithLine)
                ((ObjectWithLine) right).fillCuts(b);    
            
            
            
            
            
        }    
        setFilled(filled);    
        //owner.enableOptions(true);
    }
    public void translatePlane(int stepNum, boolean b)
    {   // als meer vlakken kies een vlak via een edge
        // als subset van origObject i.e. aan de buitenkant?
        // gebruik mousemode
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
            	
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }
                // slider weg                                
                setSlider(false, 0, 0, 0);
                //originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            if (!b)
            {   mouseMode = INERT;
            
//                owner.helpBar.setText(owner.tt("rotateText"));     
            owner.helpBar.setText(TextConstants.rotateText);

                return;
            }
            mouseMode = TRANSLATEPLANE;
            if (currentObjectGroup instanceof ObjectWithLine)
                transRotConstruct = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                transRotConstruct = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            if (numPlanes > 1)
            {
                // help message
//GWT            	
//                owner.helpBar.setText(owner.tt("selectTranslatePlaneText"));                
                //owner.topToolBar.transPlaneButton.setPressed(true);                
                transPlaneChoosen = null;
                // wait for mouse action
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                
            }
            else
            {   // find the one plane here
                for (int i = 0; i < transRotConstruct.size(); i++)
                {   Object ob = transRotConstruct.elementAt(i);
                    if (ob instanceof Plane3D)
                    {    Plane3D pl = (Plane3D) ob;
                         transPlaneChoosen = pl.copy();
                    
                    
                    }
                }
                translatePlane(1, true);
            }
        }    
        else if (stepNum == 1)
        {
            //owner.topToolBar.transPlaneButton.setPressed(false);            
            helpPoint = false;
            panel3D.hideHelpPoint();
            
            // extremen:
            // in het extreme geval snijdt het vlak origObject in een
            // vertex (convexiteit)
            // vindt dus de vertices met de kleinste(-) en grootste(+)
            // plane position t.o.v. vlak??????
            
// is originalObject updated?            
            minTrans = 0;
            maxTrans = 0;
            Line3D normalLine = new Line3D(transPlaneChoosen.point,
                Vector3D.plus(transPlaneChoosen.point,
                              transPlaneChoosen.normal));  
            for (int i = 0; i < originalObject.numVertices; i++)
            {   Plane3D normalPlane = new Plane3D(
                    transPlaneChoosen.normal.x, 
                    transPlaneChoosen.normal.y,
                    transPlaneChoosen.normal.z,
                    Vector3D.dotProduct(transPlaneChoosen.normal,
                        originalObject.vertices[i]));
                int isType = Plane3D.intersectionType(normalLine, 
                                                      normalPlane);                      
                // isType == 2 levert 0
                if (isType == 1)
                {   Vector3D v = Plane3D.getIntersectionPoint(
                        normalLine, normalPlane);
                    double pos = 
                        Vector3D.dotProduct(transPlaneChoosen.normal, v) -
                        Vector3D.dotProduct(transPlaneChoosen.normal, 
                            transPlaneChoosen.point);    
                    if (pos < minTrans)
                        minTrans = pos;
                    if (pos > maxTrans)
                        maxTrans = pos;
                }    
            }
        
            // wat betekenen deze getallen nu??
            // verschuif over een vector tussen 
            // transPlaneChoosen.normal * minTrans
            // en transPlaneChoosen.normal * maxTrans
            
//System.out.println("minTrans = " + UF.format(minTrans, 3));
//System.out.println("maxTrans = " + UF.format(maxTrans, 3));

// hier min ophogen, max verlagen
// zeg met 5% van de range?
            minTrans += Vector3D.NZero;
            maxTrans -= Vector3D.NZero;


//            double rangeFac = (maxTrans - minTrans) * 5e-2d;
//            minTrans += rangeFac;
//            maxTrans -= rangeFac;

            transPlane = transPlaneChoosen.copy();

            setSlider(true, 0, minTrans, maxTrans);
                        
//            owner.helpBar.setText(owner.tt("rotateText"));
            owner.helpBar.setText(TextConstants.rotateText);
        
        // kijk bij rebuild uit dat je niet twee keer hetzelfde
        // vlak snijdt (er kan een parallel vlak zijn)
        
        // verschuiven via een vector loodrecht op vlak
        // neem normaalvector en twee constanten
        
        // rebuild, etc. gebeurt in processSlider
        // ook original opnieuw zetten
        
        
        }
    }    

    public void rotatePlane(int stepNum, boolean b)
    {   // als meer vlakken kies een vlak EN draaias via edge 
        // aan de buitenkant??
        // zijn er meer vlakken, kies er dan een of weer vragen?
        // gebruik mousemode      
        if (stepNum == 0)
        {
            // hier of andere relevante knoppen "doof maken"
            // of als je wat anders aanklikt drawline aborteren
            // zo kun je ook drawLine opnieuw starten
            if (mouseMode != INERT)
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);

                }
                // slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            if (!b)
            {   mouseMode = INERT;
            
//                owner.helpBar.setText(owner.tt("rotateText"));
            	owner.helpBar.setText(TextConstants.rotateText);
                
                return;
            }
            mouseMode = ROTATEPLANE;
            //owner.topToolBar.rotPlaneButton.setPressed(true);                            
            if (currentObjectGroup instanceof ObjectWithLine)
                transRotConstruct = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                transRotConstruct = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            // altijd klikken voor draaias
//GWT            
//            owner.helpBar.setText(owner.tt("selectRotatePlaneText"));
            // help message
            rotPlaneChoosen = null;
            rotLine = null;
            helpPoint = true;
            panel3D.helpPointColor = DrawConstants.planeOutlineColor;
            
        }
        else if (stepNum == 1)
        {
           //owner.topToolBar.rotPlaneButton.setPressed(false);                            
           helpPoint = false;
           panel3D.hideHelpPoint();
            
        // extremen:
        // 1) as is een edge van origobject: zoek hoek (<180) tussen
        // de facets, geeft samen met hoek vlak min en max
        // beginstand is hoek vlak
        
// is originalObject updated?

        Facet3D f = originalObject.facetContaining(
            rotLine.point1, rotLine.point2, true);
        if (f != null)
        {
//System.out.println("original edge");        
            Facet3D adjf = originalObject.facetContaining(
                rotLine.point2, rotLine.point1, true);
            Vector3D fNormal = new Vector3D(f.normal);
            Vector3D adjfNormal = new Vector3D(adjf.normal);
            Vector3D.makeUnitary(fNormal);
            Vector3D.makeUnitary(adjfNormal);
            double angle = Math.acos(
                Vector3D.dotProduct(fNormal, adjfNormal));
//System.out.println("angle = " + angle);                
            angle = Math.PI - angle;
//System.out.println("angle = " + UF.format(angle * 360 / (2 * Math.PI), 0));                                                           

// rotLine has direction as segment of f
            double dot = Vector3D.dotProduct(fNormal, rotPlaneChoosen.normal);
            double angle2 = Math.acos(dot);
            double angle2A = Math.acos(- dot);
                
// rotLine has direction opposite to segment of adjf                
            double angle3 = angle - angle2;
            double angle3A = angle - angle2A;

// rotating is clockwise relative to segment of adjf                
                
//            if (angle2 > angle)
//                angle2 = Math.PI - angle2;
//System.out.println("angle2 = " + UF.format(angle2 * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle2A = " + UF.format(angle2A * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle3 = " + UF.format(angle3 * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle3A = " + UF.format(angle3A * 360 / (2 * Math.PI), 0));                                

//System.out.println("angle2+3 = " + (angle2 + angle3));                                

        if (angle2 > angle2A)
        {   minRot = - angle2A;
            maxRot = angle3A;
        }
        else
        {   minRot = - angle2;
            maxRot = angle3;
        }
        
            
// bijna goed!!            
//System.out.println("Min+Max = " + 
//    UF.format((Math.abs(minRot) + Math.abs(maxRot)) * 360 / (2 * Math.PI), 0));                                                                            

//            minRot += Vector3D.NZero;
//            maxRot -= Vector3D.NZero;
//System.out.println("minRot = " + UF.format(minRot * 360 / (2 * Math.PI), 0));                                
//System.out.println("maxRot = " + UF.format(maxRot * 360 / (2 * Math.PI), 0));                                
//            double rangeFac = (maxRot - minRot) * 1e-2d;
//            minRot += rangeFac;
//            maxRot -= rangeFac;

        }
        else
        {   f = currentObjectGroup.facetContaining(
                rotLine.point1, rotLine.point2, false);
            if (f != null) 
            {   boolean replacesOrigObject = false;
                if (currentObjectGroup instanceof ObjectWithPlane)
                    replacesOrigObject =
                    ((ObjectWithPlane) currentObjectGroup).replacesOrigObject(f);
                else if (currentObjectGroup instanceof ObjectWithLine)
                    replacesOrigObject =
                    ((ObjectWithLine) currentObjectGroup).replacesOrigObject(f);
                    
                if (replacesOrigObject)
                {
//System.out.println("replacement edge");        
                    Vector3D fNormal = new Vector3D(f.normal);
                    Vector3D.makeUnitary(fNormal);
                    
//System.out.println("fNormal " + fNormal.toString());
//System.out.println("rotPlaneNormal " + rotPlaneChoosen.normal.toString());
                    
// f contains rotLine as segment                    
                    double dotProd =
                        Vector3D.dotProduct(fNormal, rotPlaneChoosen.normal);                    
                    double angle2 = Math.acos(dotProd);
                    double angle2A = Math.acos(- dotProd);
// rotation clockwise opposite to segment              
                    
                    double angle3 = Math.PI - angle2;
                    double angle3A = Math.PI - angle2A;

//System.out.println("angle2 = " + UF.format(angle2 * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle2A = " + UF.format(angle2A * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle3 = " + UF.format(angle3 * 360 / (2 * Math.PI), 0));                                
//System.out.println("angle3A = " + UF.format(angle3A * 360 / (2 * Math.PI), 0));                                



//if (modelCode <= owner.ICOSAHEDRON)
//{
        if (angle2 > angle2A)
        {   minRot = - angle3A;
            maxRot = angle2A;
        }
        else
        {   minRot = - angle3;
            maxRot = angle2;
        }
/*


// hier paramsurfaces apart?
// ook bij draaien om een segment
}
else
{
        if (angle2 > angle2A)
        {   minRot = - angle3A;
            maxRot = angle2A;
        }
        else
        {   minRot = - angle2;
            maxRot = angle3;
        }
}
*/

//System.out.println("Min+Max = " + 
//    UF.format((Math.abs(minRot) + Math.abs(maxRot)) * 360 / (2 * Math.PI), 0));                                                                            

//System.out.println("minRot = " + UF.format(minRot * 360 / (2 * Math.PI), 0));                                
//System.out.println("maxRot = " + UF.format(maxRot * 360 / (2 * Math.PI), 0));                                

            //minRot += Vector3D.NZero;
            //maxRot -= Vector3D.NZero;
                    
                    
//                    double rangeFac = (maxRot - minRot) * 5e-2d;
//                    minRot += rangeFac;
//                    maxRot -= rangeFac;
                    
                }
                else
                {
//System.out.println("inner edge");                                    
                    minRot = -Math.PI / 2;
                    maxRot = Math.PI / 2;
                }
            }    
        // 2) as IN een facet van origObject
        // dan max - min = 180, beginstand is hoek vlak  
        }
        // kijk bij rebuild uit dat je niet twee keer hetzelfde
        // vlak snijdt (er kan een parallel vlak zijn)
        
        // rebuild
        // original opnieuw zetten
            rotPlane = rotPlaneChoosen.copy();

            setSlider(true, 0, minRot, maxRot);
            
//            owner.helpBar.setText(owner.tt("rotateText"));
            owner.helpBar.setText(TextConstants.rotateText);
       
        
        }
    }    

    public void showCut(int stepNum, boolean b)
    {   //showCut = b;
        // als meer vlakken kies een vlak
        // gebruik mousemode,  
        if (stepNum == 0)
        {
// voorlopig even zo,
// eventueel uitzondering voor rotate, translate?
//
            oldMouseMode = mouseMode;
//System.out.println("oldMouseMode = " + oldMouseMode);                    
            if ((mouseMode != INERT) && 
                (mouseMode != TRANSLATEPLANE) &&
                (mouseMode != ROTATEPLANE)
                ) 
            {   // zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                // deze niet!
//                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {	
//GWT                    
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);
                }
                // slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            //showCut = b;
            if (!b)
            {   showCut = false;
                // hiding
                if ((mouseMode != TRANSLATEPLANE) &&
                    (mouseMode != ROTATEPLANE))
                    mouseMode = INERT;
                    
                owner.helpBar.setText(TextConstants.rotateText);
//System.out.println("hiding cut, mouseMode = " + mouseMode);
//GWT                
//                owner.helpBar.setText(owner.tt("rotateText"));
//                owner.topToolBar.showCutButton.setPressed(false);                                
                setCutPanel(showCut);
                return;   
            }    
            // nodig voor selectie
            mouseMode = SHOWHIDECUT;
//GWT            
//            owner.enableOptions(false);
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
//System.out.println("numPlanes = " + numPlanes);                
            if (numPlanes > 1)
            {
                // help message
            	owner.helpBar.setText(TextConstants.selectShowCutText);
//GWT            	
//                owner.helpBar.setText(owner.tt("selectShowCutText"));                
//                owner.topToolBar.showCutButton.setPressed(true);                                
                cutPlaneChoosen = null;
                // wait for mouse action
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                
            }
            else
            {   // find the one plane here
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Plane3D)
                    {   Plane3D pl = (Plane3D) ob;
                        cutPlaneChoosen = pl.copy();
                    }
                }
                showCut(1, true);
            }            
        }
        else if (stepNum == 1)
        {
//GWT        	
//           owner.topToolBar.showCutButton.setPressed(false);                            
           helpPoint = false;
           panel3D.hideHelpPoint();
            
            showCut = b;
            
//GWT            
//           owner.topToolBar.showCutButton.setImage(owner.hideCutImage);
            
//ObjectGroup3D cutGroup = ObjectWithPlane.getCut(currentObjectGroup, cutPlane);
// tijdelijk
cutPlane = cutPlaneChoosen.copy();
//updateCutPanel();
setCutPanel(true);
updateCutPanel();
        // open een nieuwe Object3DContainer
        // maak een deepCopy van de cut en alles wat de cut
        // replaced
        // en draai dit object totdat het "plat" ligt
        // aanpassen na translate en rotate, delete, draw
        if ((oldMouseMode != TRANSLATEPLANE) &&
            (oldMouseMode != ROTATEPLANE))
            mouseMode = INERT;        
        else
            mouseMode = oldMouseMode;
        
        owner.helpBar.setText(TextConstants.rotateText);
//GWT        
//        owner.helpBar.setText(owner.tt("rotateText"));
//        owner.enableOptions(true);
//System.out.println("cut choosen, mouseMode = " + mouseMode);                            
        
        }
    }    

    public void cutObject(int stepNum, boolean b)
    {   
    	
System.out.println("make foldout " + stepNum + " " + b);

    	// als meer vlakken kies een vlak
        // gebruik mousemode
        if (stepNum == 0)
        {
            if (mouseMode != INERT)
            {   
            	
System.out.println("mm = " + mouseMode);

            	// zet model gelijk aan current
                // dit aborteert andere LOPENDE muis acties
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);
                
                // deze NIET!!
                //owner.topToolBar.cutButton.setDown(false);                                
   
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
//GWT                
//                owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
                cutObjectGroup = null;
                if (numPlanes > 0)
                {   
                    //owner.topToolBar.transPlaneButton.setImage(owner.transPlaneImage);
                    //owner.topToolBar.rotPlaneButton.setImage(owner.rotPlaneImage);

                }
                
                // slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                helpPoint = false;
                DrawConstants.TICKSVISIBLE = false;                    
                showHelpPointDrop(false);                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            setCutPanel(false);
            figureCut = b;            
            // plakken
            if (!figureCut)
            {   mouseMode = INERT;
            
            	owner.helpBar.setText(TextConstants.rotateText);
//GWT            
//                owner.enableOptions(true);
                setCutPanel(showCut);
                setNumLines(numLines);
                setNumPlanes(numPlanes);
                
	            planesFilled = oldPlanesFilled;
    	        fillPlanes(planesFilled);
	            if (planesFilled)
	            {
	            	owner.topToolBar.planesFilledButton.setDown(true);
//	            	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
	            
	            }
	            else
	            {    
	            	owner.topToolBar.planesFilledButton.setDown(false);
//	            	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
	            
	            }
        
        		if (historyPointer > 0)
        		{   owner.rightToolBar.undoButton.setEnabled(true);                
        		}
    	        owner.rightToolBar.redoButton.setEnabled(false);                
    	            
//GWT    	        
//                owner.topToolBar.cutButton.setPressed(false);                                            
                panel3D.testString = "";
                //addToHistory();
                
                return;   
            }    
            mouseMode = CUTOBJECT;
//GWT            
//            owner.enableOptions(false);
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            if (numPlanes > 1)
            {
                // help message
                owner.helpBar.setText(TextConstants.selectCutPlaneText);
            	
            	//GWT            	
//                owner.topToolBar.cutButton.setPressed(true);                                                            
                planeChoosen = null;
                // wait for mouse action
                helpPoint = true;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
            }
            else
            {   // find the one plane here
                for (int i = 0; i < construction.size(); i++)
                {   Object ob = construction.elementAt(i);
                    if (ob instanceof Plane3D)
                        planeChoosen = ((Plane3D) ob).copy();
                }
                cutObject(1, true);
            }            
            
            
        }
        else if (stepNum == 1)
        {
//GWT        	
//            owner.topToolBar.cutButton.setPressed(false);                                                        
            panel3D.hideHelpPoint();
            helpPoint = false;
// hier zijvlak onderscheppen
// via return en alles terugzetten
boolean isSide = false;    
for (int i = 0; i < originalObject.numFacets; i++)
{   Vector3D sideNormal = new Vector3D(originalObject.facets[i].normal);
    Plane3D sidePlane = new Plane3D(sideNormal.x, sideNormal.y,
        sideNormal.z, Vector3D.dotProduct(sideNormal,
            originalObject.facets[i].points[0]));
    if (sidePlane.equals(planeChoosen))
        isSide = true;
}    
if (isSide)
{   planeChoosen = null; 
	figureCut = false;
    return;
}
            double totalVolume = originalObject.getVolume();

     
            cutObjectGroup = ObjectWithPlane.cutObjectGroup(
                currentObjectGroup, planeChoosen);
                
            oldPlanesFilled = planesFilled;    
            fillPlanes(false);    
            
            panel3D.initializeModel(cutObjectGroup, false);
            
            owner.helpBar.setText(TextConstants.selectCutFigureText);

            owner.topToolBar.disableLineButtons();            
            owner.topToolBar.disablePlaneButtons2();
            //owner.topToolBar.cutButton.setEnabled(true);
//GWT            
//            owner.topToolBar.cutButton.setImage(owner.glueImage);                
            owner.rightToolBar.undoButton.setEnabled(false);
            owner.rightToolBar.redoButton.setEnabled(false);
            
            ObjectGroup3D left = (ObjectGroup3D) cutObjectGroup.objects.elementAt(0);
            ObjectGroup3D right = (ObjectGroup3D) cutObjectGroup.objects.elementAt(1);
            Object3D topLeft = left.leftMostLeaf();
            Object3D topRight = right.leftMostLeaf();
            
            double leftVolume = topLeft.getVolume();
            double rightVolume = topRight.getVolume();
            
            double leftPerc = (leftVolume / totalVolume) * 100;
            double largePerc = leftPerc;
            if (largePerc < 50)
                largePerc = 100 - largePerc;
//GWT
/*            
panel3D.testString = owner.tt("volumeText") + ": " +
                     owner.tt("largeFigureText") + " " +
                     UF.format(largePerc, 1) + "%, " +
                     owner.tt("smallFigureText") + " " +
                     UF.format(100 - largePerc, 1) + "%"
                     ;
*/                     

        // neem originalObject
        // snij dit door
        // resultaat beetje verschuiven
        // dan beide weer rebuilden volgens recipe
        // denk erom dat je het recipe ook verschuift!
        // dan weer wachten op muisactie eventueel om figuur te kiezen
        
        
        }
        else if (stepNum == 2)
        {

//System.out.println("step 2");            
            ObjectGroup3D left = (ObjectGroup3D) cutObjectGroup.objects.elementAt(0);
            ObjectGroup3D right = (ObjectGroup3D) cutObjectGroup.objects.elementAt(1);
            Object3D topLeft = left.leftMostLeaf();
            Object3D topRight = right.leftMostLeaf();
            Vector leftConstruction = new Vector();
            if (left instanceof ObjectWithPlane)
                leftConstruction = ((ObjectWithPlane) left).getConstruction();
            else if (left instanceof ObjectWithLine)
                leftConstruction = ((ObjectWithLine) left).getConstruction();
            Vector rightConstruction = new Vector();
            if (right instanceof ObjectWithPlane)
                rightConstruction = ((ObjectWithPlane) right).getConstruction();
            else if (right instanceof ObjectWithLine)
                rightConstruction = ((ObjectWithLine) right).getConstruction();
                
            Vector newLeftConstruction = new Vector();
            Vector newRightConstruction = new Vector();
            
            if (left.containsFacet(facetChoosen) >= 0)
            {               
                ObjectWithPlane.letterObject(topLeft);
//System.out.println("topleftcenter = " + topLeft.center.toString());

                Vector3D leftTrans = Vector3D.minus(
                    new Vector3D(0,0,0), topLeft.center);
                    
//System.out.println("leftTrans = " + leftTrans.toString());                    

                topLeft.center();    
                
//System.out.println("leftcenter = " + left.center.toString());                
                
                //topLeft.diameter = topLeft.getDiameter();
                int nLines = 0;
                int nPlanes = 0;
                for (int i = 0; i < leftConstruction.size(); i++)
                {   Object conObj = leftConstruction.elementAt(i);
                    if (conObj instanceof Plane3D)
                    {   conObj = ((Plane3D) conObj).translateBy(leftTrans);
                        newLeftConstruction.addElement(conObj);
                        nPlanes++;
                    }
                    else if (conObj instanceof Line3D)
                    {   conObj = ((Line3D) conObj).translateBy(leftTrans);
                        newLeftConstruction.addElement(conObj);
                        nLines++;
                    }
                }
//System.out.println("leftco = " + newLeftConstruction.size());                                
                //topLeft.setTickMarks(TICKNUM);
                currentObjectGroup = rebuild(topLeft, newLeftConstruction, null);
//currentObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();                                
                originalObject = currentObjectGroup.leftMostLeaf();

//System.out.println("leftrebuildcenter = " + currentObjectGroup.center.toString());                                
                //currentObjectGroup = left;
                //originalObject = topLeft;
                cutObjectGroup = null;
                addToHistory();                
                
            planesFilled = oldPlanesFilled;
            fillPlanes(planesFilled);
            if (planesFilled)
            {    owner.topToolBar.planesFilledButton.setDown(true);
//            	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
            
            }
            else
            {    owner.topToolBar.planesFilledButton.setDown(false);
//            	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
            
            }
                
panel3D.testString = "";                
                panel3D.initializeModel(currentObjectGroup, false);                                    
                mouseMode = INERT;
                
                owner.helpBar.setText(TextConstants.rotateText);
                
                setNumLines(nLines);
                setNumPlanes(nPlanes);
                owner.rightToolBar.undoButton.setEnabled(true);                                
                if (numPlanes > 0)                
                {    
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);
                
                }
//GWT                
//                owner.enableOptions(true);                        
//                owner.resetHelpPoints(); 
                setHelpPointDrop(false);                
//System.out.println("left");                            

// numPlanes numLines

            }
            else if (right.containsFacet(facetChoosen) >= 0)
            {   
            
                ObjectWithPlane.letterObject(topRight);
//if (topRight.center.equals(topRight.getCenter()))
//System.out.println("right equal");
            
                Vector3D rightTrans = Vector3D.minus(
                    new Vector3D(0,0,0), topRight.center);
                topRight.center();    
                
                //topRight.diameter = topRight.getDiameter();
                int nLines = 0;
                int nPlanes = 0;
                for (int i = 0; i < rightConstruction.size(); i++)
                {   Object conObj = rightConstruction.elementAt(i);
                    if (conObj instanceof Plane3D)
                    {   conObj = ((Plane3D) conObj).translateBy(rightTrans);
                        newRightConstruction.addElement(conObj);
                        nPlanes++;
                    }
                    else if (conObj instanceof Line3D)
                    {   conObj = ((Line3D) conObj).translateBy(rightTrans);
                        newRightConstruction.addElement(conObj);
                        nLines++;
                    }
                }
//System.out.println("rightco = " + newRightConstruction.size());                
                //topRight.setTickMarks(TICKNUM);
                currentObjectGroup = rebuild(topRight, newRightConstruction, null);
//currentObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();                
                originalObject = currentObjectGroup.leftMostLeaf();
                cutObjectGroup = null;
                addToHistory();
                
            planesFilled = oldPlanesFilled;
            fillPlanes(planesFilled);
            if (planesFilled)
            {    owner.topToolBar.planesFilledButton.setDown(true);
//            	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
            
            }
            else
            {    owner.topToolBar.planesFilledButton.setDown(false);
//            	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
            
            }

                
panel3D.testString = "";                                
                //currentObjectGroup = right;
                //originalObject = topRight;
                panel3D.initializeModel(currentObjectGroup, false);                                    
                mouseMode = INERT;
                
//                owner.helpBar.setText(owner.tt("rotateText"));
                owner.helpBar.setText(TextConstants.rotateText);
                
                setNumLines(nLines);
                setNumPlanes(nPlanes);
                owner.rightToolBar.undoButton.setEnabled(true);                                
                if (numPlanes > 0)
                {    
//GWT                	
//                	owner.topToolBar.cutButton.setImage(owner.cutImage);
                
                }
//GWT                
//                owner.enableOptions(true);    
//                owner.resetHelpPoints();
                setHelpPointDrop(false);
//System.out.println("right");                                            
// numPlanes numLines

        // na stap 2 weer mouseMode = INERT
        
            }
//System.out.println("nothing");                                        
        }
    }
    
    public void undo()
    {   if (mouseMode != INERT)
        {   // zet model gelijk aan current
            // dit aborteert andere LOPENDE muis acties

    		owner.topToolBar.drawLineButton.setDown(false);                
    		owner.topToolBar.deleteLineButton.setDown(false);                                
    		owner.topToolBar.drawPlaneButton.setDown(false);                                
    		owner.topToolBar.parPlaneButton.setDown(false);
    		owner.topToolBar.deletePlaneButton.setDown(false);
    		owner.topToolBar.showCutButton.setDown(false);                
    		owner.topToolBar.cutButton.setDown(false);                                
    		owner.rightToolBar.conDrawButton.setDown(false);                                                
    	
    	
            tempObjectGroup = null;
            tempObjectGroup2 = null;                
            foldOutObjectGroup = null;
//GWT            
//            owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
            cutObjectGroup = null;
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                        
            panel3D.hideHelpPoint();
            helpPoint = false;
            DrawConstants.TICKSVISIBLE = false;                        
            showHelpPointDrop(false);                            
//            setCutPanel(false);
        }    
        mouseMode = INERT;
       
//        owner.helpBar.setText(owner.tt("rotateText"));
        owner.helpBar.setText(TextConstants.rotateText);
        
        previousObjectGroup();
//        fillPlanes(planesFilled);
//        setNumLines(numLines);
//        setNumPlanes(numPlanes);
        if ((numLines > 0) && (DrawConstants.llFactor > 0))
            owner.topToolBar.shortLinesButton.setEnabled(true);
        if (numPlanes > 0)
        {   
            //owner.topToolBar.transPlaneButton.setImage(owner.transPlaneImage);
            //owner.topToolBar.rotPlaneButton.setImage(owner.rotPlaneImage);
        }
//GWT        
//        owner.enableOptions(true);
        
// kijk of         
        if (showCut)
        {   boolean hasCutPlane = false;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    hasCutPlane = ((ObjectWithLine) currentObjectGroup).containsPlane(cutPlane);
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {    hasCutPlane = ((ObjectWithPlane) currentObjectGroup).containsPlane(cutPlane);
//System.out.println("OWP");      
//System.out.println("has = " + hasCutPlane);
            }
            if (!hasCutPlane)
            {    killCutPanel();
//System.out.println("killed");            
            }
            else
            {    updateCutPanel();
            }
                
        }    
        
    }    


    public void redo()
    {   if (mouseMode != INERT)
        {   // zet model gelijk aan current
            // dit aborteert andere LOPENDE muis acties
        	owner.topToolBar.drawLineButton.setDown(false);                
        	owner.topToolBar.deleteLineButton.setDown(false);                                
        	owner.topToolBar.drawPlaneButton.setDown(false);                                
        	owner.topToolBar.parPlaneButton.setDown(false);
        	owner.topToolBar.deletePlaneButton.setDown(false);
        	owner.topToolBar.showCutButton.setDown(false);                
        	owner.topToolBar.cutButton.setDown(false);                                
        	owner.rightToolBar.conDrawButton.setDown(false);
        	
            tempObjectGroup = null;
            tempObjectGroup2 = null;                
            foldOutObjectGroup = null;
//GWT            
//            owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
            cutObjectGroup = null;
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                        
            panel3D.hideHelpPoint();
            helpPoint = false;
            DrawConstants.TICKSVISIBLE = false;                            
            showHelpPointDrop(false);                            

        }    
        mouseMode = INERT;
        
//        owner.helpBar.setText(owner.tt("rotateText"));
        owner.helpBar.setText(TextConstants.rotateText);
        
        nextObjectGroup();
//        fillPlanes(planesFilled);
//        setNumLines(numLines);
//        setNumPlanes(numPlanes);
        if ((numLines > 0) && (DrawConstants.llFactor > 0))
            owner.topToolBar.shortLinesButton.setEnabled(true);
        if (numPlanes > 0)
        {   
            //owner.topToolBar.transPlaneButton.setImage(owner.transPlaneImage);
            //owner.topToolBar.rotPlaneButton.setImage(owner.rotPlaneImage);
        }
//GWT        
//        owner.enableOptions(true);
        
// kijk of         
        if (showCut)
        {   boolean hasCutPlane = false;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    hasCutPlane = ((ObjectWithLine) currentObjectGroup).containsPlane(cutPlane);
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {    hasCutPlane = ((ObjectWithPlane) currentObjectGroup).containsPlane(cutPlane);
//System.out.println("OWP");      
//System.out.println("has = " + hasCutPlane);
            }
            if (!hasCutPlane)
            {    killCutPanel();
//System.out.println("killed");            
            }
            else
            {    updateCutPanel();
            }
                
        }    
        
    }    

    public boolean escapeActive()
    {   
//System.out.println("ea");                        
        return ((mouseMode != INERT) &&
                !((mouseMode == FOLDOUT) && (startFacet != null)) &&
                !((mouseMode == CUTOBJECT) && (planeChoosen != null)) &&
                !((mouseMode == SHOWHIDECUT) && (cutPlaneChoosen != null))
               );
    }
    
    
    public void rotate()
    {   
//System.out.println("rotate");                        
//if (startFacet == null)
//System.out.println("sf = null");                        
//else
//System.out.println("sf != null");                        
//System.out.println("mm = " + mouseMode);                        
        if ((mouseMode != INERT) &&
            !((mouseMode == FOLDOUT) && (startFacet != null)) &&
            !((mouseMode == CUTOBJECT) && (planeChoosen != null)) &&
            !((mouseMode == SHOWHIDECUT) && (cutPlaneChoosen != null))
           )
        {   // zet model gelijk aan current
            // dit aborteert andere LOPENDE muis acties
            owner.topToolBar.drawLineButton.setDown(false);                
            owner.topToolBar.deleteLineButton.setDown(false);                                
            owner.topToolBar.drawPlaneButton.setDown(false);                                
            owner.topToolBar.parPlaneButton.setDown(false);
            owner.topToolBar.deletePlaneButton.setDown(false);
            owner.topToolBar.showCutButton.setDown(false);                
            owner.topToolBar.cutButton.setDown(false);                                
            owner.rightToolBar.conDrawButton.setDown(false);                                                
            
//System.out.println("rotated");                
            tempObjectGroup = null;
            tempObjectGroup2 = null;                
            foldOutObjectGroup = null;
            startFacet = null;
//GWT            
//            owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
            cutObjectGroup = null;
            // slider weg                                
            setSlider(false, 0, 0, 0);
            originalObject = currentObjectGroup.leftMostLeaf();
            panel3D.hideHelpLine();                        
            panel3D.hideHelpPoint();
            helpPoint = false;
            DrawConstants.TICKSVISIBLE = false;                                
            showHelpPointDrop(false);                            
            panel3D.initializeModel(currentObjectGroup, false);                    
            mouseMode = INERT;
            
            owner.helpBar.setText(TextConstants.rotateText);
//GWT            
//            owner.helpBar.setText(owner.tt("rotateText"));
//            owner.enableOptions(true);
        }    
//        else
//System.out.println("not rotated");                        
        
    }    

/*    
    public void setBounds(int x, int y, int b, int h)
    {
    	super.setBounds(x, y, b, h);
    	updateWork();
    }
*/
    
/*    
    // update workSpace after resizing
    public void updateWork()
    {   
        // nieuwe afmeting panel3D
        if (showCut)
        {
            panel3D.setBounds(getSize().width / 2, 0, 
                              getSize().width / 2, getSize().height);
            //panel3D.offscreen = null;    
            panel3D.resetModel();
            cutPanel.setBounds(0, 0, getSize().width / 2, getSize().height);
            //cutPanel.offscreen = null;    
            cutPanel.resetModel();
        }    
        else
        {
            panel3D.setBounds(0, 0, getSize().width, getSize().height);
            //panel3D.offscreen = null;    
            panel3D.resetModel();
//System.out.println("resetModel");       
            
            owner.toolsButton.setLocation(5, panel3D.getSize().height - 35);
            owner.resetButton.setLocation(5, 5);
        }
        if (slider != null)            
        {   if (flatButton != null)
            {  flatButton.setLocation(
                    panel3D.getSize().width - flatButton.getSize().width,
                    0);
                    //panel3D.getSize().height - flatButton.getSize().height);
                slider.setLocation(
                    panel3D.getSize().width - slider.getSize().width
                        - flatButton.getSize().width,
                    0);    
                    //panel3D.getSize().height - slider.getSize().height);

            }
            else
            {
                slider.setLocation(
                    panel3D.getSize().width - slider.getSize().width,
                    0);
                    //panel3D.getSize().height - slider.getSize().height);
            }                
        }                
        if (dropButton != null)
        {   
            dropButton.setLocation(panel3D.getSize().width - 
                                   dropButton.getSize().width, 0);
            
        }                           
        if (dropMenu != null)        
            dropMenu.setLocation(panel3D.getSize().width - 
                                 dropMenu.getSize().width, 
                                 dropButton.getSize().height);

        repaint();
        
    }  // updateWork  
*/

/*    
    // private method to find a darker or a brighter version of color c, using the
    // HSB color model; factor determines the amount of change, a negative
    // factor produces darker colors, a positive factor brighter colors
    public static Color hsbChange(Color c, int factor)
    {   // array for storing hue, saturation, brigtness
        float[] hsbValues = new float[3];
        // the resulting variant of Color c
        Color result;
        // find hsbValues for Color c
        hsbValues = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
                    hsbValues);
        // if a darker color is wanted
        if (factor < 0)
        {   // if possible decrease brightness by |factor|*0.1
            if (hsbValues[2] >= -factor * 1e-1f)
                hsbValues[2] -= -factor * 1e-1f;
            // else try to increase saturation by |factor|*0.1        
            else    
                if (hsbValues[1] <= 1.0f + factor * 1e-1f)
                    hsbValues[1] += -factor * 1e-1f;
        }
        else // a brighter color is wanted
        {   // if possible increase brightness by factor*0.1
            if (hsbValues[2] <= 1.0f - factor * 1e-1f)
                hsbValues[2] += factor * 1e-1f;
            // else try to decrease saturation by factor*0.1        
            else    
                if (hsbValues[1] >= factor * 1e-1f)
                    hsbValues[1] -= factor * 1e-1f;
        }
        // get the resulting color in the RGB model            
        result = Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]);            
        return result;
    }
*/         

/*
    public void invalidate()
    {   super.invalidate();
        //offscreen = null;
    }
*/
    public boolean vertexAllowed(FacetWithVertex fwv)
    {
        // ESSENTIELE RESTRICTIE voor alle versies       
        if (DoorzienGWT.version == DoorzienGWT.FI)
        {
                boolean isOnExt = false;
                if (panel3D.model instanceof ObjectWithLine)
                    isOnExt = ((ObjectWithLine) panel3D.model).
                        vertexOnLineExtension(fwv.vertex);
                else if (panel3D.model instanceof ObjectWithPlane)
                    isOnExt = ((ObjectWithPlane) panel3D.model).
                        vertexOnLineExtension(fwv.vertex);
                else if (panel3D.model instanceof ObjectWithPoint)
                    isOnExt = ((ObjectWithPoint) panel3D.model).
                        vertexOnLineExtension(fwv.vertex);
                         
                return !isOnExt;                
        }
        // RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN        
        else if (DoorzienGWT.version == DoorzienGWT.EPN)
        {
//setStart();
            boolean isOnOrig = true;
            if (panel3D.model instanceof ObjectWithLine)
                isOnOrig = ((ObjectWithLine) panel3D.model).
                    vertexOnOrigObject(fwv.vertex, fwv.facet);
            else if (panel3D.model instanceof ObjectWithPlane)
                isOnOrig = ((ObjectWithPlane) panel3D.model).
                    vertexOnOrigObject(fwv.vertex, fwv.facet);
            else if (panel3D.model instanceof ObjectWithPoint)
                isOnOrig = ((ObjectWithPoint) panel3D.model).
                    vertexOnOrigObject(fwv.vertex, fwv.facet);
//showTime("checking vertex");
            return isOnOrig;
        }
        return false;
    }

    public boolean edgePointAllowed(FacetWithEdgePoint fwep)
    {    
        // ESSENTIELE RESTRICTIE voor alle versie       
        if (DoorzienGWT.version == DoorzienGWT.FI)
        {
            boolean isOnExt = false;
            if (panel3D.model instanceof ObjectWithLine)
                isOnExt = ((ObjectWithLine) panel3D.model).
                    edgeOnLineExtension(fwep.edgeWithPoint[0], fwep.edgeWithPoint[1]);
            else if (panel3D.model instanceof ObjectWithPlane)
                isOnExt = ((ObjectWithPlane) panel3D.model).
                    edgeOnLineExtension(fwep.edgeWithPoint[0], fwep.edgeWithPoint[1]);
            else if (panel3D.model instanceof ObjectWithPoint)
                 isOnExt = ((ObjectWithPoint) panel3D.model).
                    edgeOnLineExtension(fwep.edgeWithPoint[0], fwep.edgeWithPoint[1]);
            return !isOnExt;                                    
        }    
        // RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN
        else if (DoorzienGWT.version == DoorzienGWT.EPN)        
        {
//setStart();
            boolean isOnOrig = true;
            if (panel3D.model instanceof ObjectWithLine)
                isOnOrig = ((ObjectWithLine) panel3D.model).
                    edgeOnOrigObject(fwep.edgeWithPoint[0], 
                        fwep.edgeWithPoint[1], fwep.facet);
            else if (panel3D.model instanceof ObjectWithPlane)
                isOnOrig = ((ObjectWithPlane) panel3D.model).
                    edgeOnOrigObject(fwep.edgeWithPoint[0], 
                        fwep.edgeWithPoint[1], fwep.facet);
            else if (panel3D.model instanceof ObjectWithPoint)
                isOnOrig = ((ObjectWithPoint) panel3D.model).
                     edgeOnOrigObject(fwep.edgeWithPoint[0], 
                        fwep.edgeWithPoint[1], fwep.facet);
//showTime("checking edge");                                
            return isOnOrig;
        }
        return false;
    }    
    

    public void flattenAction()
    {
        // roteer de foldOutGroup in view space
        Vector3D from = new Vector3D(
            startFacet.unitNormal.x,
            startFacet.unitNormal.y,
            startFacet.unitNormal.z);
//System.out.println("from = " + from.toString());            
        Vector3D to = new Vector3D(0, 0, 1);
        panel3D.vwRotate(from, to);
//System.out.println("new from = " + startFacet.unitNormal.toString());                        
        

//ook (tijdelijk) parallele projectie??

        // zet slider op 100% (maakt maximale foldout)
        
        processSlider(1);
        slider.setPosition(1);
    	
    }
/*
	class ListChangeHandler implements ChangeHandler
	{
		//public void onMouseDown(MouseDownEvent e)
		public void onChange(ChangeEvent e)
		{
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();
			
			//int index = dropBox.getSelectedIndex();
		}
	}	
*/			

	
	class FlatCL implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			flattenAction();
			flattened = true;
		}
	}
			
/*    
    // inner class for listening to the flatButton
    class FlatML extends MouseAdapter
    {   public void mousePressed(MouseEvent e)
        {   
            // roteer de foldOutGroup in view space
            Vector3D from = new Vector3D(
                startFacet.unitNormal.x,
                startFacet.unitNormal.y,
                startFacet.unitNormal.z);
//System.out.println("from = " + from.toString());            
            Vector3D to = new Vector3D(0, 0, 1);
            panel3D.vwRotate(from, to);
//System.out.println("new from = " + startFacet.unitNormal.toString());                        
            

// ook (tijdelijk) parallele projectie??

            // zet slider op 100% (maakt maximale foldout)
            
            processSlider(1);
            slider.setPosition(1);
            flattened = true;
        }    
    }
*/    
    
//GWT
/*    
    // inner class for DropButton
    class DropML extends MouseAdapter
    {   public void mousePressed(MouseEvent e)
        {   
    	
    		panel3D.remove(dropMenu);
            panel3D.add(dropMenu);
            panel3D.repaint();
    
        }
    }
*/    
    
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
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - drawingPanelCanvas.getAbsoluteTop();
				
				if (DrawConstants.SSTT != DrawConstants.touchSSTT)
				{
					DrawConstants.SSTT = DrawConstants.touchSSTT;
				}
				
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
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
			    
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
    	if (!draaibaar)
    		return;

        	xClicked = eventX;
            yClicked = eventY;
//GWT                
//            panel3D.remove(dropMenu);                
            if (mouseMode == INERT)
            {   panel3D.oldX = eventX;
                panel3D.oldY = eventY;
//                startF = panel3D.clickedFacet(e.getX(), e.getY());
                xStart = eventX;
                yStart = eventY;
                dragging = true;
                
            }    
            else if (mouseMode == DRAWLINE) 
            {   // ObjectWithPoint finds only visible facets containing
                // this vertex so use the top object in the tree
                   
                FacetWithVertex fwv = 
                    panel3D.facetWithVertexClicked(xClicked, yClicked);
                    
                // restrictions here
                // 1) only vertices of originalObject (visible or invisible)
                // 2 only vertices of tempObjectGroup die op
                //   op originalObject liggen, d.w.z. je mag nieuw
                //  punten op edges ook gebruiken
                // 2A geen verlengde lijnen, verder alles
                // DIT MOET
                // 3) free i.e. any visible(!) vertex of tempObjectGroup
                                          
                if (fwv != null)
                {   
                    if (vertexAllowed(fwv))    
                    {
                        // process this point
                        clickedPoint = fwv.vertex;
                        drawLine(pointsSelected + 1, true);
                    }
                }    
                else // no vertex clicked, check for an edge
                {   // ObjectWithPoint finds only visible facets containing
                    // this edge so use the top object in the tree                        
                       
                    FacetWithEdgePoint fwep = 
                        panel3D.facetWithEdgePointClicked(xClicked, yClicked);
                            
                    // restrictions here
                    // 1) only points on the edges of origObject
                    // beter:
                    // 2) points on edges replacing edges of origObject
                    // gebruik containsDirSegment o.i.d. 
                    // 2A) geen punten op verlengde lijnen
                    // 3) any point on any edge                      
                    if (fwep != null)
                    {
                        if (edgePointAllowed(fwep))    
                        {
                            // process this edge with new point
                            clickedEdgeWithPoint = fwep.edgeWithPoint;
                            drawLine(pointsSelected + 1, true);
                        }
                    }
                    else // nothing relevant clicked
                    {
                        panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                }    
                            
            } // mouseMode == DRAWLINE
            
            else if (mouseMode == DELETELINE)
            {
                Vector3D[] edgeWithPoint = 
                    panel3D.edgeClicked(xClicked, yClicked);
                if (edgeWithPoint != null)
                {   Line3D line = new Line3D(
                        edgeWithPoint[0],
                        edgeWithPoint[1]);
                    if (construction.contains(line))
                    {   lineChoosen = line;
                        deleteLine(1, true);
                    }
                    else // rotate
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
                        //startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }
                else // nothing relevant clicked
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }    
            
            }
            else if (mouseMode == DRAWPLANE) 
            {   // ObjectWithPoint finds only visible facets containing
                // this vertex so use the "last" object in the tree
                FacetWithVertex fwv = 
                    panel3D.facetWithVertexClicked(xClicked, yClicked);
                // restrictions here
                // 1) only vertices of originalObject (visible or invisible)
                // 2 only vertices of tempObjectGroup die op
                //   op originalObject liggen, d.w.z. je mag nieuw
                //  punten op edges ook gebruiken
                // 2A geen verlengde lijnen, verder alles
                // DIT MOET
                // 3) free i.e. any visible(!) vertex of tempObjectGroup
                if (fwv != null)
                {
                	
//System.out.println("fwv not null");
                	
                    if (vertexAllowed(fwv))    
                    {
                    	
//System.out.println("fwv allowed");

                        // process this point
                        clickedPoint = fwv.vertex;
                        drawPlane(pointsSelected + 1, true);
                    }
                    
                }    
                else // no vertex clicked, check for an edge
                {   // ObjectWithPoint finds only visible facets containing
                    // this edge so use the "last" object in the tree                        

                    FacetWithEdgePoint fwep = 
                        panel3D.facetWithEdgePointClicked(xClicked, yClicked);
                    // restrictions here
                    // 1) only points on the edges of origObject
                    // beter:
                    // 2) points on edges replacing edges of origObject
                    // gebruik containsDirSegment o.i.d. 
                    // 2A) geen punten op verlengde lijnen
                    // DIT MOET
                    // 3) any point on any edge                      
                    if (fwep != null)
                    {   
//System.out.println("fwep not null");                    	
                    	
                        if (edgePointAllowed(fwep))    
                        {
                            // process this edge with new point
                            clickedEdgeWithPoint = fwep.edgeWithPoint;
                            drawPlane(pointsSelected + 1, true);
                        }
                    }
                    else // nothing relevant clicked
                    {
                        panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }    
            } // mouseMode == DRAWPLANE
            else if (mouseMode == DRAWPARPLANE)
            {   
                if (parPlaneChoosen != null)
                // get the point
                {   
//System.out.println("ppc != null");                                                
                    // ObjectWithPoint finds only visible facets containing
                    // this vertex so use the "last" object in the tree
                    FacetWithVertex fwv = 
                        panel3D.facetWithVertexClicked(xClicked, yClicked);
                    // restrictions here
                    // 1) only vertices of originalObject (visible or invisible)
                    // 2 only vertices of tempObjectGroup die op
                    //   op originalObject liggen, d.w.z. je mag nieuw
                    //  punten op edges ook gebruiken
                    // 2A geen verlengde lijnen, verder alles
                    // DIT MOET
                    // 3) free i.e. any visible(!) vertex of tempObjectGroup
                    if (fwv != null)
                    {   
                        if (vertexAllowed(fwv))    
                        {
                            // process this point
                            clickedPoint = fwv.vertex;
                            drawParPlane(1, true);
                        }
                    
                    }    
                    else // no vertex clicked, check for an edge
                    {   // ObjectWithPoint finds only visible facets containing
                        // this edge so use the "last" object in the tree                        

                        FacetWithEdgePoint fwep = 
                            panel3D.facetWithEdgePointClicked(xClicked, yClicked);
                        // restrictions here
                        // 1) only points on the edges of origObject
                        // beter:
                        // 2) points on edges replacing edges of origObject
                        // gebruik containsDirSegment o.i.d. 
                        // 2A) geen punten op verlengde lijnen
                        // DIT MOET
                        // 3) any point on any edge                      
                        if (fwep != null)
                        {   
                            if (edgePointAllowed(fwep))    
                            {
                                // process this edge with new point
                                clickedEdgeWithPoint = fwep.edgeWithPoint;
                                drawParPlane(1, true);
                            }
                        }
                        else // nothing relevant clicked
                        {
                            panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                            xStart = eventX;
                            yStart = eventY;
                        
                            dragging = true;                            
                        }
                    }    
                }
                else // parPlaneChoosen == null
                // so get the plane
                {
                    
//System.out.println("ppc = null");                        
                    Vector3D[] edgeWithPoint = 
                        panel3D.edgeClicked(xClicked, yClicked);
                    if (edgeWithPoint != null)
                    {   Line3D line = new Line3D(edgeWithPoint[0], edgeWithPoint[1]);
                        for (int i = 0; i < construction.size(); i++)
                        {   Object ob = construction.elementAt(i);
                            if (ob instanceof Plane3D)
                            {   Plane3D plane = ((Plane3D) ob).copy();
                                int isType = Plane3D.intersectionType(line, plane);
                                if (isType == 2)    
                                {   parPlaneChoosen = plane.copy();
                                    //lineChoosen = line;
                                }    
                                // else clicked on an edge not 
                                // on a plane
                            }
                        }
                        if (parPlaneChoosen != null)
                        {   
                            movedPoint = null;
                            movedEdgeWithPoint = null;
                            clickedPoint = null;
                            clickedEdgeWithPoint = null;
                                
                            owner.helpBar.setText(TextConstants.parPlanePointText);                                                                
                            // wait for mouse action choosing the point

                        }
                        else // rotate
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }
                    }    
                    else // nothing relevant clicked
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                }
            } // mouseMode == DRAWPARPLANE   
            else if (mouseMode == DELETEPLANE)
            {
                Vector3D[] edgeWithPoint = 
                    panel3D.edgeClicked(xClicked, yClicked);
                if (edgeWithPoint != null)
                {   Line3D line = new Line3D(
                        edgeWithPoint[0],
                        edgeWithPoint[1]);
                    for (int i = 0; i < construction.size(); i++)
                    {   Object ob = construction.elementAt(i);
                        if (ob instanceof Plane3D)
                        {   Plane3D plane = (Plane3D) ob;
                            int isType = Plane3D.intersectionType(
                                line, plane);
                            if (isType == 2)    
                            {   planeChoosen = plane.copy();
                            }    
                        }
                    }
                    if (planeChoosen != null)
                        deletePlane(1, true);
                    else // rotate
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }
                else // nothing relevant clicked
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }    
            
            }
            else if (mouseMode == TRANSLATEPLANE)
            {   if (transPlaneChoosen != null)
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }
                else // transPlaneChoosen == null
                {
                    Vector3D[] edgeWithPoint = 
                        panel3D.edgeClicked(xClicked, yClicked);
                    if (edgeWithPoint != null)
                    {   Line3D line = new Line3D(
                            edgeWithPoint[0],
                            edgeWithPoint[1]);
                        for (int i = 0; i < transRotConstruct.size(); i++)
                        {   Object ob = transRotConstruct.elementAt(i);
                            if (ob instanceof Plane3D)
                            {   Plane3D plane = (Plane3D) ob;
                                int isType = Plane3D.intersectionType(
                                    line, plane);
                                if (isType == 2)    
                                {   transPlaneChoosen = plane.copy();
                                    //lineChoosen = line;
                                }    
                            }
                        }
                        if (transPlaneChoosen != null)
                            translatePlane(1, true);
                        else // rotate
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }
                    }    
                    else // nothing relevant clicked
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                }
            }
            else if (mouseMode == ROTATEPLANE)
            {   if (rotPlaneChoosen != null)
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }
                else // rotPlaneChoosen == null
                {
                    Vector3D[] edgeWithPoint = 
                        panel3D.edgeClicked(xClicked, yClicked);
                    if (edgeWithPoint != null)
                    {   Line3D line = new Line3D(
                            edgeWithPoint[0],
                            edgeWithPoint[1]);
                        for (int i = 0; i < transRotConstruct.size(); i++)
                        {   Object ob = transRotConstruct.elementAt(i);
                            if (ob instanceof Plane3D)
                            {   Plane3D plane = (Plane3D) ob;
                                int isType = Plane3D.intersectionType(
                                    line, plane);
                                if (isType == 2)    
                                {   rotPlaneChoosen = plane.copy();
                                    rotLine = line;
                                }    
                            }
                        }
                        if (rotPlaneChoosen != null)
                            rotatePlane(1, true);
                        else // rotate
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }
                    }    
                    else // nothing relevant clicked
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                }
            }
            else if (mouseMode == SHOWHIDECUT)
            {   if (cutPlaneChoosen != null)
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }
                else // cutPlaneChoosen == null
                {
                    Vector3D[] edgeWithPoint = 
                        panel3D.edgeClicked(xClicked, yClicked);
                    if (edgeWithPoint != null)
                    {   Line3D line = new Line3D(
                            edgeWithPoint[0],
                            edgeWithPoint[1]);
                        for (int i = 0; i < construction.size(); i++)
                        {   Object ob = construction.elementAt(i);
                            if (ob instanceof Plane3D)
                            {   Plane3D plane = (Plane3D) ob;
                                int isType = Plane3D.intersectionType(
                                    line, plane);
                                if (isType == 2)    
                                {   cutPlaneChoosen = plane.copy();
                                    //clickedEdgeWithPoint = edgeWithPoint;
                                }    
                            }
                        }
                        if (cutPlaneChoosen != null)
                            showCut(1, true);
                        else // rotate
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }
                    }    
                    else // nothing relevant clicked
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                }
            }
            else if (mouseMode == CUTOBJECT)
            {   if (planeChoosen != null)
                {   Facet3D fChoosen = 
                        panel3D.clickedFacet(xClicked, yClicked);
                    if (fChoosen != null)
                    {   facetChoosen = fChoosen;
                        cutObject(2, true);
                    }
                    else
                    {
                        panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }
                else // planeChoosen == null
                {
                    Vector3D[] edgeWithPoint = 
                        panel3D.edgeClicked(xClicked, yClicked);
                    if (edgeWithPoint != null)
                    {   Line3D line = new Line3D(
                            edgeWithPoint[0],
                            edgeWithPoint[1]);
                        for (int i = 0; i < construction.size(); i++)
                        {   Object ob = construction.elementAt(i);
                            if (ob instanceof Plane3D)
                            {   Plane3D plane = (Plane3D) ob;
                                int isType = Plane3D.intersectionType(
                                    line, plane);
                                if (isType == 2)    
                                {   planeChoosen = plane.copy();
                                    //lineChoosen = line;
                                }    
                            }
                        }
                        if (planeChoosen != null)
                            cutObject(1, true);
                        else // rotate
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }
                    }    
                    else // nothing relevant clicked
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                                                        
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }    
                } // planeChoosen == null
            }
            
            else if (mouseMode == FOLDOUT)
            {   // als vlakje gekozen draaien
                if (startFacet != null)
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
//                    startF = panel3D.clickedFacet(e.getX(), e.getY());                                                    
                    xStart = eventX;
                    yStart = eventY;
                    
                    dragging = true;                            
                }
                else // startFacet == null
                {   Facet3D temp = panel3D.clickedFacet(eventX, eventY);
                    if (temp != null)
                    {
                        // check if this facet makes any sense
                        if (foldOutObjectGroup instanceof ObjectWithPlane)
                        {   ObjectWithPlane owp = (ObjectWithPlane) foldOutObjectGroup;
                            if (owp.replacesOrigObject(temp))
                            {   while (temp.isReplacementOf != null)
                                    temp = temp.isReplacementOf;
                                startFacet = temp;    
                            }    
                        }
                        else if (foldOutObjectGroup instanceof ObjectWithLine)
                        {   ObjectWithLine owl = (ObjectWithLine) foldOutObjectGroup;
                            if (owl.replacesOrigObject(temp))
                            {   while (temp.isReplacementOf != null)
                                    temp = temp.isReplacementOf;
                                startFacet = temp;    
                            }    
                        
                        }
                        else // dummy group with foldOutObject
                        {   startFacet = temp;
                        }    
                        // facet does not make sense
                        if (startFacet == null)
                        {   panel3D.oldX = eventX;
                            panel3D.oldY = eventY;
//                            startF = panel3D.clickedFacet(e.getX(), e.getY());                                                            
                            xStart = eventX;
                            yStart = eventY;
                            
                            dragging = true;                            
                        }    
                        else
                            makeFoldOut(1, true);
                    }
                    else // nothing clicked, dragging
                    {
                        panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
//                        startF = panel3D.clickedFacet(e.getX(), e.getY());                            
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }
            
            }

//        } // other button(s)

	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
       	if (dragging)
        {

            int xCenter = panel3D.breedte / 2; //getSize().width / 2;
            int yCenter = panel3D.hoogte / 2; //getSize().height / 2;
            int minRad = Math.min(xCenter, yCenter);
            
            inCircle = Math.sqrt((xStart - xCenter) * (xStart - xCenter) +
                                 (yStart - yCenter) * (yStart - yCenter)) < minRad * RADFACTOR;

            if ((eventX <= 0) || (eventY <= 0) || (eventX >= panel3D.breedte) ||
                (eventY >= panel3D.hoogte))
            {    
                dragging = false;
                return;    
            }
                
            if (inCircle)
            {
                    
// hier checken voor buiten beeld                    

//System.out.println("dragging");
//dit doet WireFrame met 360 i.p.v. 180 graden
                double xTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.breedte; //getSize().width;
                double yTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.hoogte; //getSize().height;
// Peter's versie
//                double xTheta = (panel3D.oldY - e.getY()) * 5e-1d;
//                double yTheta = (panel3D.oldX - e.getX()) * 5e-1d;

               
// ECHT draaien
/*
                double xChange = panel3D.oldY - e.getY();
                double yChange = panel3D.oldX - e.getX();
                double theta = 5e-1d * Math.sqrt(
                    xChange * xChange + yChange * yChange);
                Vector3D axis = new Vector3D(xChange, yChange, 0);    
                // axis is no zo gekozen dat er theta gedraaid moet worden
                // met de klok mee gezien vanuit axis
*/
                                
                panel3D.rotateBy(xTheta, yTheta);
                panel3D.updateHelpPoint(eventX, eventY);
                panel3D.updateHelpLine(eventX, eventY);
//                panel3D.rotateBy(theta, axis);
//                panel3D.rotateCake(xTheta, yTheta);
                panel3D.repaint();
                
                panel3D.oldX = eventX; //.getX();
                panel3D.oldY = eventY; //.getY();
            }
            else // not inCircle
            {
                    
// hier afkappen voor buiten beeld                    
                    // choose correct direction
                double centerX = ((double) panel3D.breedte) / 2; //getSize().width) / 2;
                double centerY = ((double) panel3D.hoogte) / 2; //getSize().height) / 2;                    
                double xTheta = 0;
                double yTheta = 0;

                if (eventX < centerX)
                    yTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.hoogte; //getSize().height;
                else                    
                    yTheta = (eventY - panel3D.oldY) * 180.0d / panel3D.hoogte; //getSize().height;
                if (eventY < centerY)             
                    xTheta = (eventX - panel3D.oldX) * 180.0d / panel3D.breedte; //getSize().width;
                else                 
                    xTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.breedte;//getSize().width;
                                     
// Peter's versie
//                double xTheta = (panel3D.oldY - e.getY()) * 5e-1d;
//                double yTheta = (panel3D.oldX - e.getX()) * 5e-1d;

                    double zTheta = 0;
                    
                if (Math.abs(yTheta) > Math.abs(xTheta))
                    zTheta = yTheta;
                else
                    zTheta = xTheta;
                    
                panel3D.rotateByZ(zTheta);
                panel3D.updateHelpPoint(eventX, eventY);                    
                panel3D.updateHelpLine(eventX, eventY);                    
                panel3D.repaint();
                
                panel3D.oldX = eventX; //e.getX();
                panel3D.oldY = eventY; //e.getY();
                    
            }    
        } // if (dragging)
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
  
	
	class CutMouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		boolean dragging = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			if (!draaibaar)
    			return;
    	
    	
    		cutPanel.oldX = e.getX();
            cutPanel.oldY = e.getY();
            dragging = true;
            
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			if (dragging)
            {
                if ((e.getX() <= 0) || (e.getY() <= 0) ||
                    (e.getX() >= cutPanel.breedte) ||
                    (e.getY() >= cutPanel.hoogte) 
                    )
                {    
                    dragging = false;
                    return;    
                }
                // choose correct direction
                double centerX = ((double) cutPanel.breedte) / 2;
                double centerY = ((double) cutPanel.hoogte) / 2;                    
                double xTheta = 0;
                double yTheta = 0;

                if (e.getX() < centerX)
                    yTheta = (cutPanel.oldY - e.getY()) * 180.0d /
                             cutPanel.hoogte;
                else                    
                    yTheta = (e.getY() - cutPanel.oldY) * 180.0d /
                             cutPanel.hoogte;
                if (e.getY() < centerY)             
                    xTheta = (e.getX() - cutPanel.oldX) * 180.0d /
                             cutPanel.breedte;
                else                 
                    xTheta = (cutPanel.oldX - e.getX()) * 180.0d /
                             cutPanel.breedte;
                double zTheta = 0;
                  
                if (Math.abs(yTheta) > Math.abs(xTheta))
                    zTheta = yTheta;
                else
                    zTheta = xTheta;
                    
                cutPanel.rotateByZ(zTheta);
                //cutPanel.paint(cutPanel.getGraphics());
                cutPanel.repaint();
                cutPanel.oldX = e.getX();
                cutPanel.oldY = e.getY();
            }
			
			//int eventX = e.getX();
			//int eventY = e.getY();
			
			//mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			if (dragging)
            {
                // make sure the dragg-event-queue for rotating is completed!!
                cutPanel.repaint();
                dragging = false;
            }

		}

	} //MouseHandler

	class CutTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		boolean dragging = false;
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - cutPanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - cutPanelCanvas.getAbsoluteTop();
				
				if (DrawConstants.SSTT != DrawConstants.touchSSTT)
				{
					DrawConstants.SSTT = DrawConstants.touchSSTT;
				}
				
				if (!draaibaar)
        			return;
        	
        		cutPanel.oldX = eventX;
                cutPanel.oldY = eventY;
                dragging = true;
				
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
			    int eventX = touch.getPageX() - cutPanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - cutPanelCanvas.getAbsoluteTop();				
			    
				if (dragging)
	            {
	                if ((eventX <= 0) || (eventY <= 0) ||
	                    (eventX >= cutPanel.breedte) ||
	                    (eventY >= cutPanel.hoogte) 
	                    )
	                {    
	                    dragging = false;
	                    return;    
	                }
	                // choose correct direction
	                double centerX = ((double) cutPanel.breedte) / 2;
	                double centerY = ((double) cutPanel.hoogte) / 2;                    
	                double xTheta = 0;
	                double yTheta = 0;

	                if (eventX < centerX)
	                    yTheta = (cutPanel.oldY - eventY) * 180.0d /
	                             cutPanel.hoogte;
	                else                    
	                    yTheta = (eventY - cutPanel.oldY) * 180.0d /
	                             cutPanel.hoogte;
	                if (eventY < centerY)             
	                    xTheta = (eventX - cutPanel.oldX) * 180.0d /
	                             cutPanel.breedte;
	                else                 
	                    xTheta = (cutPanel.oldX - eventX) * 180.0d /
	                             cutPanel.breedte;
	                double zTheta = 0;
	                  
	                if (Math.abs(yTheta) > Math.abs(xTheta))
	                    zTheta = yTheta;
	                else
	                    zTheta = xTheta;
	                    
	                cutPanel.rotateByZ(zTheta);
	                //cutPanel.paint(cutPanel.getGraphics());
	                cutPanel.repaint();
	                cutPanel.oldX = eventX;
	                cutPanel.oldY = eventY;
	            }

				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			if (dragging)
            {
                // make sure the dragg-event-queue for rotating is completed!!
                cutPanel.repaint();
                dragging = false;
            }
		}

	}
    
    
/*    
    // inner class for cutPanel
    class CutMLMML extends MouseAdapter implements MouseMotionListener
    {   boolean dragging = false; 
        public void mousePressed(MouseEvent e)
        {       
        		if (!draaibaar)
        			return;
        	
        	
        		cutPanel.oldX = e.getX();
                cutPanel.oldY = e.getY();
                dragging = true;
        }
        public void mouseReleased(MouseEvent e)
        {   if (dragging)
            {
                // make sure the dragg-event-queue for rotating is completed!!
                cutPanel.repaint();
                dragging = false;
            }
        
        }
        public void mouseDragged(MouseEvent e)
        {   if (dragging)
            {
                if ((e.getX() <= 0) || (e.getY() <= 0) ||
                    (e.getX() >= cutPanel.breedte) ||
                    (e.getY() >= cutPanel.hoogte) 
                    )
                {    
                    dragging = false;
                    return;    
                }
                // choose correct direction
                double centerX = ((double) cutPanel.breedte) / 2;
                double centerY = ((double) cutPanel.hoogte) / 2;                    
                double xTheta = 0;
                double yTheta = 0;

                if (e.getX() < centerX)
                    yTheta = (cutPanel.oldY - e.getY()) * 180.0d /
                             cutPanel.hoogte;
                else                    
                    yTheta = (e.getY() - cutPanel.oldY) * 180.0d /
                             cutPanel.hoogte;
                if (e.getY() < centerY)             
                    xTheta = (e.getX() - cutPanel.oldX) * 180.0d /
                             cutPanel.breedte;
                else                 
                    xTheta = (cutPanel.oldX - e.getX()) * 180.0d /
                             cutPanel.breedte;
                double zTheta = 0;
                  
                if (Math.abs(yTheta) > Math.abs(xTheta))
                    zTheta = yTheta;
                else
                    zTheta = xTheta;
                    
                cutPanel.rotateByZ(zTheta);
                //cutPanel.paint(cutPanel.getGraphics());
                cutPanel.repaint();
                cutPanel.oldX = e.getX();
                cutPanel.oldY = e.getY();
            }
        }    
        public void mouseMoved(MouseEvent e)
        {   // nothing to do
        }    
        public void mouseEntered(MouseEvent e)
        {   
//GWT        	
//        	cutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));               
        }    
        public void mouseExited(MouseEvent e)
        {   
//GWT        	
//        	cutPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   
        }    
    } // class CutMLMML
*/    
	
	
} // class DrawingPanel2

class FoldOutTreeNode implements Serializable
{   // tree attributes
    // parent
    FoldOutTreeNode parentNode = null;
    // children of type FoldOutTreeNode
    Vector childNodes = new Vector();
    
    // info attributes
    // the facet represented by the node
    Facet3D facet;
    // the fold out component, elements of type facet
    // en wel dit facet en die van de kinderen,
    // met toegevoegd alle ZICHTBARE facets die deze facets vervangen 
    Vector foldOutFacets = new Vector();
    // (minimum) angle and rotation axis relative
    // to parentNode.facet
    double minAngle;
    double currentAngle; // foldout relative to minAngle
    // axis is common edge to facet in parentNode
    // remember the indices of the edgepoints
    // in this facet with the correct orientation!!
    // this because the axis is also rotated!!
    int axisFrom, axisTo;
    
// tijdelijk
int level;
    
    // constructor
    public FoldOutTreeNode(Facet3D f, double angle, int aFrom, int aTo)
    {   facet = f;
        minAngle = angle;
        axisFrom = aFrom;
        axisTo = aTo;
    }
    
    
    
}
