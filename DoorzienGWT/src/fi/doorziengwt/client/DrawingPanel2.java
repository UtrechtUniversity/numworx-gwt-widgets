package fi.doorziengwt.client;


import java.util.*;

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

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;


/**
 * class for the main drawing area; the class contains a Canvas for drawing and intercepting
 * Mouse and Touch Events on the Canvas, and a second Canvas which is only used to show
 * the cut of a 3d-object; this class also contains all methods for manipulating 3d-objects; 
 * manipulating the 3d-object in general works as follows:<br>
 * all changes to the 3d-object need Mouse/Touch actions, e.g. selecting points to put a plane
 * through; the tool bars call the relevant method, e.g. drawPlane in which the mouseMode
 * is changed to (e.g.) DRAWPLANE and the program waits for (repeated) Mouse/Touch action, which can be
 * handled correctly since the mouseMode is known     
 * @author huub
 */
public class DrawingPanel2 extends LayoutPanel
{   
	/**
	 * owner, handles button actions from the toolbars
	 */
    DoorzienGWT owner;

    /**
     * drawing the main 3d-object and a cut (if needed) 
     */
    Object3DContainer panel3D;
    Object3DContainer cutPanel;
    
    /**
     * default model code
     */
    int modelCode = DoorzienGWT.CUBE;

    /**
     * zoom parameters
     */
    public static double MAXZOOM = 15e-1d;
    public static double MINZOOM = 2e-1d; 
    public static double ZOOMSTEP = 1e-1d;
    public static double defaultZoom = 8e-1d;
    /**
     * actual zoom
     */
    public double zoom = defaultZoom;

    /**
     * projections
     */
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    /**
     * actual projection
     */
    public int projection = CENTRALPROJ;
    
    /**
     * should the object be shown as a solid (or a wireframe)
     */
    boolean filled = false;
    /**
     * should planes be filled or transparent
     */
    boolean planesFilled = false;

    /**
     * should a cut be shown
     */
    boolean showCut = false;
    /**
     * is the object cut into two pieces?
     */
    boolean figureCut = false;

    /**
     * mouse mode for rotating
     */
    public static final int INERT = 0;
    /**
     * line mouse modes
     */
    public static final int DRAWLINE = 1;
    public static final int DELETELINE = 2;
    
    /**
     * plane mouse modes
     */
    public static final int DRAWPLANE = 5;
    public static final int DRAWPARPLANE = 12;    
    public static final int DELETEPLANE = 6;
    
    /**
     * cut(ting) mouse modes
     */
    public static final int SHOWHIDECUT = 9; 
    public static final int CUTOBJECT = 10;    
    
    /**
     * mouse mode for fold out
     */
    public static final int FOLDOUT = 11;        

    /**
     * default mouse mode
     */
    public int mouseMode = INERT;
    /**
     * remembering a mouse mode
     */
    public int oldMouseMode;
    
    /**
     * the initial object
     */
    Object3D originalObject;
    /**
     * the ObjectGroup3D representing the manipulated initial object  
     */
    ObjectGroup3D currentObjectGroup;
    /**
     * temporary copies of originalObject
     */
    Object3D tempOrigObject, tempOrigObject2;

    /**
     * temporary copies of currentObjectGroup;
     */
    ObjectGroup3D tempObjectGroup, tempObjectGroup2;    
    /**
     * the two halves if the object was cut into two pieces
     */
    ObjectGroup3D cutObjectGroup;

    /**
     * drawing lines and planes
     */
    int pointsSelected = 0;
    Vector3D point1, point2, point3;
    /**
     * clicked vertex or clicked point on an edge (not a vertex)
     */
    Vector3D clickedPoint;
    Vector3D[] clickedEdgeWithPoint;
    /**
     * idem for preview, not implemented
     */
    Vector3D movedPoint;
    Vector3D[] movedEdgeWithPoint;
    
    /**
     * a selected line, e.g. for deleting
     */
    Line3D lineChoosen;
    /**
     * a selected plane, e.g. for deleting
     */
    Plane3D planeChoosen;

    /**
     * the recipe for the current object
     */
    Vector construction; 
    /**
     * the color recipe for the current object
     */
    Vector constructionColors;

    /**
     * plane selected to be copied to a parallel plane  
     */
    Plane3D parPlaneChoosen;
    /**
     * selected point of the parallel plane to be constructed
     */
    Vector3D parPointChoosen;

    /**
     * plane selected for showing cut or cutting object 
     */
    Plane3D cutPlane, cutPlaneChoosen;
    /**
     * remembering planesFilled
     */
    boolean oldPlanesFilled;

    /**
     * facet chosen as center of fold out
     */
    Facet3D facetChoosen;
    
    /**
     * number of lines in currentObjectGroup3D
     */
    int numLines = 0;
    /**
     * number of planes in currentObjectGroup3D
     */
    int numPlanes = 0;

    /**
     * making a fold out: copies of initial object and  
     * currentObjectGroup3D to be used for the fold out 
     */
    Object3D foldOutObject;
    ObjectGroup3D foldOutObjectGroup;
    /**
     * central facet of the fold out
     */
    Facet3D startFacet = null;
    
    /**
     * keeping track of facets during construction of fold out  
     */
    boolean[] facetsUsed;
    
    /**
     * root of the fold out tree, see class FoldOutTreeNode 
     */
    FoldOutTreeNode foldOutTreeRoot;

    /**
     * initial fold out factor (just a little folded open)
     */
    double foldOutInit = 2e-1d;

    /**
     * actual fold out factor
     */
    double currentFoldOut;

    /**
     * fill of object before becoming a fold out
     */
    boolean oldFilled;

    /** 
     * is the fold out flattened?
     */
    boolean flattened = false;
    /**
     * position of object before becoming a fold out
     */
    Matrix3D oldPos;

    /**
     * value of the fold out slider 
     */
    double sliderValue = 0;
    /**
     * the fold-out slider
     */
    Slider2 slider;

    /**
     * button for flattening the fold out
     */
    PushButton flatButton;
    
    /**
     * history for undo/redo
     */
    Vector history = new Vector();
    /**
     * maximum size of history
     */
    public static int MAXHISTORY = 20;
    /**
     * index in history of current object being drawn 
     */
    public int historyPointer = 0;
    
    /**
     * keeping track of mouseDown/TouchStart Events 
     */
    int xClicked;
    int yClicked;
    
    /**
     * help point for preview, not implemented 
     */
    int xMoved;
    int yMoved;
    
    /**
     * circle radius for rotate modes
     */
    public static double RADFACTOR = 1d;

    /**
     * using the preview, preview is not implemented
     */
    boolean previewOn = false;
    
    /** 
     * Sting for testing
     */
    String testString = "";

    /**
     * Canvas for drawing
     */
	Canvas drawingPanelCanvas;
	/**
	 * Context2d to draw with
	 */
	Context2d drawingPanelContext2d;
	
	/**
	 * width and height
	 */
	int breedte, hoogte;
	
	/**
	 * additional Canvas for drawing the cut by a plane
	 */
	Canvas cutPanelCanvas;
	/**
	 * Context2d for drawing on cutPanelCanvas 
	 */
	Context2d cutPanelContext2d;
	
	/**
	 * flagg for dragging
	 */
	boolean dragging = false;
	/**
	 * circle determining the type of dragging, see method MouseMoveTouchMoveAction
	 */
    boolean inCircle = false;
    /**
     * start coordinates for MouseDown/Touch Start Events
     */
    int xStart, yStart;
	
    /**
     * testing speed
     */
    public static Date date;
    public static long startTime;
    public static long endTime;

    /**
     * set start time
     */
    public static void setStart()
    {   date = new Date();
    	startTime = date.getTime();
    }    

    /**
     * get time elapsed since start time
     * @return elapsed time
     */
    public static long getTime()
    {   date = new Date();
    	endTime = date.getTime();
    	return (endTime - startTime);
    }    

    /**
     * show elepsed time in console
     * @param comment some comment
     */
    public static void showTime(String comment)
    {   System.out.println(comment + " " + getTime());
    
    }    

    /**
     * constructor: create drawing and cutPanel Canvas, add handlers
     * @param o owner
     * @param b width
     * @param h height
     * @param startModel code for initial model
     */
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
      	
    	
    	panel3D = new Object3DContainer(drawingPanelContext2d, breedte, hoogte);
    	
    	// preview is not implemented
        if (DoorzienGWT.version == DoorzienGWT.FI)
            previewOn = true;

        // slider for foldout
		slider = new Slider2(this, 0, 1);
		add(slider.sliderCanvas);
    	setWidgetLeftWidth(slider.sliderCanvas, breedte - Slider2.horSize - 1, Style.Unit.PX, Slider2.horSize, Style.Unit.PX);
		setWidgetTopHeight(slider.sliderCanvas, 1, Style.Unit.PX, Slider2.vertSize, Style.Unit.PX);
		slider.setVisible(false);
		
		// button to flatten foldout
		flatButton = new PushButton("plat");
		flatButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		add(flatButton);
    	setWidgetLeftWidth(flatButton, breedte - 40 - 1, Style.Unit.PX, 40, Style.Unit.PX);
		setWidgetTopHeight(flatButton, 1 + Slider2.vertSize, Style.Unit.PX, 22, Style.Unit.PX);
		flatButton.setVisible(false);
		flatButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		flatButton.addClickHandler(new FlatCL());
		
        setNewModel(startModel);
        
    }  // constructor  


    /**
     * set the projection
     * @param proj code for projection
     */
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
    
    /**
     * set lettering vertices
     * @param b true/false
     */
    public void setLetters(boolean b)
    {   DrawConstants.letters = b;
        panel3D.repaint();
        if (cutPanel != null)
            cutPanel.repaint();
    }
    
    public void paint()
    {
    	panel3D.repaint();
        if (cutPanel != null)
            cutPanel.repaint();
    }
    
    /**
     * set the number of help points (tickmarks); note how this works: <br>
     * change the parameter DrawConstants.TICKNUM, set the help points in 
     * origianlObject and rebuild currentObjectGroup
     * @param num number of help points per edge
     */
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
    
    }

    /**
     * change the model to a new one
     * @param modelCode code for new model
     */
    public void setNewModel(int modelCode)
    {   
		// general reset
        owner.topToolBar.resetDefaults();
        numLines = 0; 
        DrawConstants.llFactor = 0;
        numPlanes = 0;
        planesFilled = false;
        // remove cutPanel3D                        
        if (showCut)
            setCutPanel(false);
        showCut = false;
        figureCut = false;
        // remove slider
        setSlider(false, 0, 0, 1);

        owner.rightToolBar.resetDefaults();

        DrawConstants.letters = false;
        // menu
        owner.resetLetters();
        setProjection(CENTRALPROJ);
        // menu
        owner.resetProjection();
        
        // help points
        DrawConstants.TICKNUM = 0;
        
        owner.resetHelpPoints();
        
        tempObjectGroup = null;
        tempObjectGroup2 = null;
        foldOutObjectGroup = null;
        cutObjectGroup = null;
        
        panel3D.hideHelpLine();                
        panel3D.hideHelpPoint();                
        panel3D.testString = "";

        mouseMode = INERT;
        history.removeAllElements();        

        // always the case?
		if (modelCode < owner.MYFIGURE)
		{
	        currentObjectGroup = makeNewModel(modelCode);        
    	    // HERE!
	        setFilled(false);        
    	    panel3D.initializeModel(currentObjectGroup, true);

	        // reset zooming HERE
    	    zoom = defaultZoom;
        	panel3D.setZoomFactor(zoom);        
        
        	addToHistory();
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        }
        
    }    

    /**
     * create a new model of type code in originalObject  
     * @param code code of the new model 
     * @return the new model as an ObjectGroup3D 
     */
    public ObjectGroup3D makeNewModel(int code)
    {   modelCode = code;
        Object3D model;
        ObjectGroup3D modelGroup;
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
        // put new model in a group        
        modelGroup = new ObjectGroup3D(model, false);
        modelGroup.numVertexLabels = model.numVertexLabels;
        return modelGroup;
    }    

    /**
     * overloaded: put Object3D object in currentObjectGroup 
     * @param object given Object3D
     * @return currentObjectGroup
     */
    public ObjectGroup3D makeNewModel(Object3D object)
    {
    	originalObject = object;

    	currentObjectGroup = new ObjectGroup3D(object, false);
    	currentObjectGroup.numVertexLabels = object.numVertexLabels;
        return currentObjectGroup;
    }
    
    /**
     * add a deep copy of currentObjectGroup to the history Vector
     */
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
        owner.rightToolBar.redoButton.setEnabled(false);            
    }

    /**
     * set currentObjectGroup to the previous ObjectGroup3D in the history Vector  
     */
    public void previousObjectGroup()
    {   int hisSize = history.size();
    
        if (hisSize > 1)
        {   historyPointer--;
            currentObjectGroup = (ObjectGroup3D) ((ObjectGroup3D) history.elementAt(historyPointer)).deepCopy();
            originalObject = currentObjectGroup.leftMostLeaf();
            // use construction to find numLines and numPlanes
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
            DrawConstants.llFactor = 0;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    DrawConstants.llFactor = ((ObjectWithLine) currentObjectGroup).getLlFactor();
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {  	DrawConstants.llFactor = ((ObjectWithPlane) currentObjectGroup).getLlFactor();
            }    
            
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

    /**
     * set currentObjectGroup to the next ObjectGroup3D in the history Vector  
     */
    public void nextObjectGroup()
    {   int hisSize = history.size();
   
        if ((hisSize - 1) > historyPointer)
        {   historyPointer++;
            currentObjectGroup = (ObjectGroup3D) ((ObjectGroup3D) history.elementAt(historyPointer)).deepCopy();
            originalObject = currentObjectGroup.leftMostLeaf();
            // use construction to find numLines and numPlanes
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
            DrawConstants.llFactor = 0;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    DrawConstants.llFactor = ((ObjectWithLine) currentObjectGroup).getLlFactor();
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {
            	DrawConstants.llFactor = ((ObjectWithPlane) currentObjectGroup).getLlFactor();
            }    
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

    /**
     * set the number of lines to nLines, inform the topToolBar
     * @param nLines new value of numLines
     */
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
    
    /**
     * set the number of planes to nPlanes, inform the topToolBar
     * @param nPlanes the new number of planes
     */
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
    
    /**
     * rebuild a start Object3D according to a recipe; use the recipe in
     * the given order! 
     * @param sObject start Object3D
     * @param recipe list with points, lines and planes 
     * @param colors list with corresponding colors
     * @return an ObjectGroup3D with points added and cut by lines and planes
     */
    public ObjectGroup3D rebuild(Object3D sObject, Vector recipe, Vector colors)
    {   Object3D start = sObject.deepCopy();
        start.setVisible(true);
        // create dummy object group
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);   
        startGroup.filled = start.filled; 
        startGroup.numVertexLabels = start.numVertexLabels;
        startGroup.fixFacetArray(); //!!!
        // now build according to recipe
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
    }
    
    /**
     * set the slider for the fold out visible/not visible; 
     * when setting to visible, also set the initial value, minimum and maximum value
     * (see also class Slider2)  
     * @param b slider visible/not visible
     * @param init initial value of the slider
     * @param min minimum value of the slider  
     * @param max maximum value of the slider
     */
    public void setSlider(boolean b, double init, double min, double max)
    {   
    	if (b)
        {   sliderValue = init;
            slider.setMinMax(min, max);
            slider.setPosition(sliderValue);
            if (mouseMode == FOLDOUT)
            {   currentFoldOut = sliderValue;
                flattened = false;
            }    
            panel3D.repaint();
        }
    	slider.setVisible(b);
    	flatButton.setVisible(b);
            
    }
    
    /**
     * the slider has been moved to a new value, make a new fold out
     * and display this object
     * @param newValue the new value of the slider
     */
    public void processSlider(double newValue)
    {   sliderValue = newValue;
        if (mouseMode == FOLDOUT)
        {	currentFoldOut = sliderValue;
            foldOut(foldOutTreeRoot, sliderValue);
            panel3D.initializeModel(foldOutObjectGroup, false);
        }
    }
    
    /**
     * add/remove the Canvas for displaying a cut: when adding the 
     * width of the drawingPanelCanvas is halved and the cutPanel
     * displayed side by side with the drawingPanelCanvas,
     * when removing, the drawingPanelCanvas takes full width  
     * @param b true/false
     */
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
    	
    }
    
    /**
     * given a plane in currentObjectGroup (cutPlane), find the
     * cut of currentObjectGroup by cutPlane, turn this into a flat
     * object and display thia object in cutPanel;
     * see method getCut() in ObjectWithPlane  
     */
    public void updateCutPanel()
    {   
        ObjectGroup3D flatModel = ObjectWithPlane.getCut(currentObjectGroup, cutPlane);

        cutPanel.mat = new Matrix3D();
        cutPanel.initializeModel(flatModel, false);
        cutPanel.setZoomFactor(zoom);        
    }

    /**
     * remove the Canvas for displaying a cut
     */
    public void killCutPanel()
    {   setCutPanel(false);
        showCut = false;
    }
    

    /**
     * display all relevant objectgroups as solid objects or
     * as wireframe objects 
     * @param b true/false
     */
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
            {   panel3D.paintType = Object3DContainer.SEMIEXACT;
                panel3D.showInside = false;
            }
            else // !planesFilled || (foldOutObjectGroup != null)
            {   panel3D.paintType = Object3DContainer.HYBRID1;            
                panel3D.showInside = true;
            }
        }    
        panel3D.repaint();    
    }    
    
    /**
     * zoom in (one ZOOMSTEP)
     */
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
    
    /**
     * zoom out (one ZOOMSTEP)
     */
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
    
    /**
     * make a fold out in two steps: <br>
     * step 0: user must chose the central facet (startFacet) of the foldout; <br>
     * Note: for EPN and cylinder or cone, Doorzien chooses the startFacet and proceeds to step 1; if
     * startFacet is not null (as in setState), also proceed to step 1<br>   
     * step 2: make the actual fold out<br>
     * Note: the fold out is made using the original object!!
     * @param stepNum number of steps (0-1)
     * @param b abort if 
     */
    public void makeFoldOut(int stepNum, boolean b)
    {   
        // button was pressed, startfacet must be choosen
        if (stepNum == 0)
        {   
            // untoggle other relevant buttons
        	if (mouseMode != INERT)
            {   
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);  
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;
                foldOutObjectGroup = null;
                cutObjectGroup = null;
                
                // remove slider                                
                setSlider(false, 0, 0, 0);
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                
                DrawConstants.TICKSVISIBLE = false;
                // aborts other active mouse modes
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            setCutPanel(false);
            // abort FOLDOUT
            if (!b)
            {   
            	mouseMode = INERT;
            	setCutPanel(showCut);
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                
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
                	owner.rightToolBar.wireSolidButton.setDown(true);
                }
                else
                {    
                	owner.rightToolBar.wireSolidButton.setDown(false);
                }

                // set to position before foldout
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

            // foldoutObjectGroup is copy of currentObjectGroup
            foldOutObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            foldOutObject = foldOutObjectGroup.leftMostLeaf(); 
            
            // make sure facets have no references to the same vertices in common, 
            // see class Object3D
            foldOutObject.loosenVertices();

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
            	makeFoldOut(1, true);
            }
            else
            {	
          	    owner.helpBar.setText(DoorzienGWT.rb.bouwplaatVlakTekst());
          	    
                // wait for mouse action
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
            }
            
        }     
        else if (stepNum == 1)
        {   
            foldOutObjectGroup.setTickMarks(0);
            panel3D.hideHelpPoint();
            
            owner.topToolBar.disableLineButtons();
            owner.topToolBar.disablePlaneButtons();
            owner.rightToolBar.undoButton.setEnabled(false);
            owner.rightToolBar.redoButton.setEnabled(false);
            
			// find index of startFacet, assumed >= 0    
			int startIndex = NoSer.containsFacet(foldOutObject, startFacet);
            
            // init facet labels as false
            facetsUsed = new boolean[foldOutObject.numFacets]; 
            // create root node, mark facet as labeled
            foldOutTreeRoot = new FoldOutTreeNode(startFacet, 0, 0, 0);
            facetsUsed[startIndex] = true;
            
            // modified fold out for cylinder and cones
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
                // this recursively constructs the whole tree
                addTreeLevel(thisLevel);
            }
            // here the fold out tree has been constructed

            // add replacements to the tree
            findRotationComponents(foldOutTreeRoot);        
        
            // set facets that were replaced to invisible
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

            // now fold the whole tree over the factor foldOutInit 
            foldOut(foldOutTreeRoot, foldOutInit);
            panel3D.showInside = true;
            // fill status befor fold out
            oldFilled = filled;
            // position of object before fold out
            oldPos = Matrix3D.copy(panel3D.mat);
            setFilled(true);
            panel3D.initializeModel(foldOutObjectGroup, false);
            
            owner.rightToolBar.wireSolidButton.setDown(true);
            // add slider
            setSlider(true, foldOutInit, 0, 1);
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            
        } // else if (stepNum == 1)
    } // makeFoldout
    
    
    
    /**
     * recursive procedure to construct a fold out:<br>
     * start with lastLevel containing only one FoldOutTreeNode which contains the startFacet, which is labeled
     * as true; then find all facets adjacent to startFacet, label them as true, put each of them in a
     * FoldOutTreeNode (together with the axis connecting this facet to the startFacet) and add all of
     * them to a new Vector thisLevel; <br>
     * then continue with lastLevel containing the FoldTreeNodes constructed in the last pass, and for each facet
     * in such FoldOutTreeNode find only those adjacent facets that have not been labeled as true, and with these
     * produce another level in the fold out tree   
     * @param lastLevel Vector containing the FoldTreeNodes constructed in the last pass 
     */
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
                // adjacent facet, could be invisible
                Facet3D adjacent = 
                    foldOutObject.facetContaining(edgeEnd, edgeStart, true);
                // find the index
                int adjIndex = foldOutObject.containsFacet(adjacent);
                // if facet is not yet labeled
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
                    
                    // rotation axis
                    int axisFrom = Facet3D.containsVertex(adjacent, edgeStart);
                    int axisTo = Facet3D.containsVertex(adjacent, edgeEnd);
                    // create a node for this facet
                    FoldOutTreeNode newNode = 
                        new FoldOutTreeNode(adjacent, minAngle, axisFrom, axisTo);
                    // fix tree structure
                    newNode.parentNode = ftn;
                    ftn.childNodes.addElement(newNode);
                    // add to this level
                    thisLevel.addElement(newNode);
                    // label facet as true
                    facetsUsed[adjIndex] = true;
                }
                // else do nothing
            }    
        }
        // recurse
        if (thisLevel.size() > 0)
            addTreeLevel(thisLevel);
        // else finished    
    }


    /**
     * version == EPN: modified fold out for a cylinder starting at a side(!) facet: 
     * add top and bottom of the cylinder to the side facet, then proceed as usual 
     * @param startNode FoldOutTreeNode containing the side facet
     */
    public void addTreeCylinderNode(FoldOutTreeNode startNode)
    {   Vector thisLevel = new Vector();
        thisLevel.addElement(startNode);      
        // get the facet of startNode
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


    /**
     * version == EPN: modified fold out for a cone starting at a side(!) facet:<br>
     * add bottom of the cone to the side facet, then proceed as usual 
     * @param startNode FoldOutTreeNode containing the side facet
     */
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

    /**
     * as the fold out was constructed using the original object, in the 
     * fold out tree, recursively substitute facets by their replacements (if any)    
     * @param startNode FoldOutTreeNode whose foldOutFacets should be fixed
     */
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
        	// always add startFacet since this contains
        	// the axisdata; should also be tehre if there are no replacements
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
    
    /**
     * recursively realize the actual folding using a foldOutFactor
     * between 0 (no folding out) and 1 (flat fold out)
     * @param startNode the FoldOutTreeNode containing the startFacet
     * @param foldOutFactor factor between 0 (no folding out) and 1 (flat fold out)
     */
    public void foldOut(FoldOutTreeNode startNode, double foldOutFactor)
    {   // skip the root completely
        if (startNode.parentNode != null)
        {   // determine angle which is needed if no folding
            // occured yet, between minAngle and Math.PI
            // relative to minAngle
            double foldAngle = 
                (Math.PI - startNode.minAngle)* foldOutFactor; 
            // adapt to current fold status    
            // note: currentAngle is also relative to minAngle
            double rotAngle = foldAngle - startNode.currentAngle;    
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
                    // this vertex has index fa.indices[k]
                    // in ob.vertices
                    ob.vertices[fa.indices[k]] = rotV;
                    fa.points[k] = rotV;                
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
    
    
    /**
     * drawing a line in stages:<br>
     * stepNum == 0: terminate other active mouse modes, make object-copies and wait 
     * for user to select the first point of the line<br>
     * stepNum == 1: process the first point of the line and wait for user to select the second
     * point of the line<br>
     * stepNum == 2: process the second point of the line and add this line to currentObjectGroup
     * (that is, cut currentObjectGroup with this line, see class ObjectWithLine)  
     * @param stepNum the step number (0,1 or 2)
     * @param b if false and stepNum == 0 abort 
     */
    public void drawLine(int stepNum, boolean b)
    {    
        // draw line button was pressed, no points chosen
        if (stepNum == 0)
        {
            // untoggle other relevant buttons
            if ((mouseMode != INERT) && 
                ((mouseMode != DRAWLINE) ||
                 (DoorzienGWT.version == DoorzienGWT.EPN))
               )  
            {                
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
                
                cutObjectGroup = null;                

                // remove slider                                
                setSlider(false, 0, 0, 0);
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                DrawConstants.TICKSVISIBLE = false;    

                // aborts other active mouse modes 
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // abort DRAWLINE            
            if (!b)
            {   owner.topToolBar.drawLineButton.setEnabled(true);
                mouseMode = INERT;
                panel3D.initializeModel(currentObjectGroup, false);
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                return;
            }
            mouseMode = DRAWLINE;
            DrawConstants.TICKSVISIBLE = true;            
                
            // copy currentObjectGroup to tempObjectGroup and show this Panel3D
            tempObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject = tempObjectGroup.leftMostLeaf();

            // for preview, not used
            tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
            tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
            
            fillPlanes(planesFilled);
            panel3D.initializeModel(tempObjectGroup, false);        
            // reset
            pointsSelected = 0;
            point1 = null;
            point2 = null;
            movedPoint = null;
            movedEdgeWithPoint = null;
            clickedPoint = null;
            clickedEdgeWithPoint = null;
            
            owner.helpBar.setText(DoorzienGWT.rb.lijnPunt1Tekst());
            
            // now wait for mouse action
        }
        else if (stepNum == 1)
        {   
        	// for preview, not implemented
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
                fillPlanes(planesFilled);                                
                panel3D.hideHelpPoint();
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // for preview, not implemented
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
                panel3D.setPreviewModel(tempObjectGroup2);
                
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved should be != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpPoint(xMoved, yMoved, DrawConstants.lineColor);
                    
                return;

            }    
            // if one of clickedPoint or clickedEdgeWithPoint
            // is not null add the point to tempObjectGroup
            else if (clickedPoint != null)
            {   
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
                
                owner.helpBar.setText(DoorzienGWT.rb.lijnPunt2Tekst());                            
            }
            else if (clickedEdgeWithPoint != null)
            {   
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
                
                owner.helpBar.setText(DoorzienGWT.rb.lijnPunt2Tekst());                            
            }
            

            // now wait again for mouse action
        }    
        else if (stepNum == 2)
        {   
        	// for preview, not implemented
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
                // line is already part of currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                // reset
                // this keeps the first point thickened                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // temporarily replace with line added
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
        	// for preview, not implemented
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
                
                // line is already part of currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
                else if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedEdgeWithPoint[2]))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                // reset
                // this keeps the first point thickened                                
                tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                
                // replace with line added
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
            // at clicking one of the moved should be  != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point1, xMoved, yMoved,
                    //new Point(xMoved, yMoved),
                    DrawConstants.lineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        DrawConstants.lineColor);
                }    
                return;

            }    
            // check here if the second point is equal to the first 
            // or if the line already exists; if not, add the line 
            else if (clickedPoint != null)
            {   // twice the same vertex clicked
                if (Vector3D.equals(point1, clickedPoint))
                {   
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // line already exists in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedPoint))
                   )
                {
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action    
                }   
                point2 = clickedPoint;   
                // now the line can be added
                currentObjectGroup = new ObjectWithLine(currentObjectGroup,
                    point1, point2, DrawConstants.lineColorIndex, DrawConstants.llFactor);
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                DrawConstants.TICKSVISIBLE = false;                    
                fillPlanes(planesFilled);                            
                panel3D.initializeModel(currentObjectGroup, false);        
                
                
            }
            else if (clickedEdgeWithPoint != null)
            {   
                // line was already part of currentObjectGroup 
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, clickedEdgeWithPoint[2]))
                   )
                {
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
                   
                panel3D.previewModel = null;                
                fillPlanes(planesFilled);                            
                DrawConstants.TICKSVISIBLE = false;
                panel3D.initializeModel(currentObjectGroup, false);        
            }    
            addToHistory();
            
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            tempObjectGroup = null;
            tempObjectGroup2 = null;
            
            setNumLines(numLines + 1);
            if (showCut)
                updateCutPanel();
            // for FI: stay in tool
            if (DoorzienGWT.version == DoorzienGWT.FI)
                drawLine(0, true);
            // for EPN: exit tool
            else if (DoorzienGWT.version == DoorzienGWT.EPN)
            {   mouseMode = INERT;
                owner.topToolBar.drawLineButton.setDown(false);
            }
        }
        
    }    
 
    /**
     * deleting a line in stages:<br>
     * stepNum == 0: terminate other active mouse modes, if there is only one line,
     * continue to stepNum == 1, otherwise wait for the user to select the line to be deleted<br>
     * stepNum == 1: delete the indicated line from currentObjectGroup using rebiuld<br>
     * @param stepNum the step number (0 or 1)
     * @param b if false and stepNum == 0 abort 
     */
    public void deleteLine(int stepNum, boolean b)
    {   
    	// button was pressed
        if (stepNum == 0)
        {  
            // untoggle other relevant buttons
            if (mouseMode != INERT)
            {               	
                owner.topToolBar.drawLineButton.setDown(false);
                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
                cutObjectGroup = null;                
                // remove slider                                
                setSlider(false, 0, 0, 0);
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                DrawConstants.TICKSVISIBLE = false;
                // aborts other active mouse modes
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // abort DELETELINE
            if (!b)
            {   mouseMode = INERT;
            	owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                return;
            }

            mouseMode = DELETELINE;
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
                        
            if (numLines > 1)
            {   // get the line via the mouse, check with construction
            	owner.helpBar.setText(DoorzienGWT.rb.verwijderLijnTekst());
                lineChoosen = null;
                panel3D.helpPointColor = DrawConstants.lineColor;
                // wait for mouse action
                
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
            panel3D.hideHelpPoint();
            
            // reconstruct without the deleted line 
            construction.removeElement(lineChoosen);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();
            
            fillPlanes(planesFilled);            
            panel3D.initializeModel(currentObjectGroup, false);        
            addToHistory();
            mouseMode = INERT;
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            tempObjectGroup = null;
            setNumLines(numLines - 1);        
            if (showCut)
                updateCutPanel();
        }
    }
    
    /**
     * lengthen all lines in the 3d-object by a factor LLSTEP (this can be done
     * repeatedly); this is done by adding 2-dimensional facets with correct
     * new intersections (if any) 
     */
    public void lengthenLines()
    {   
       
        if ((mouseMode != INERT)
            )
        {   
        	// disable other relevant buttons 
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
            cutObjectGroup = null;                
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                    
            panel3D.hideHelpPoint();
            DrawConstants.TICKSVISIBLE = false;
            // aborts other active mouse modes
            panel3D.initializeModel(currentObjectGroup, false);                    
            mouseMode = INERT;
        }
        // lengthen via rebuild since new intersection points 
        // might appear
        // get construction for rebuild
        if (currentObjectGroup instanceof ObjectWithLine)
           construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
        else if (currentObjectGroup instanceof ObjectWithPlane)
           construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
        // set new lengthen factor
        double temp = DrawConstants.llFactor + DrawConstants.LLSTEP;
        if (temp <= (DrawConstants.MAXLLFACTOR + DrawConstants.LLSTEP / 10))
        	DrawConstants.llFactor = temp;
        // rebuild
        currentObjectGroup = rebuild(originalObject, construction, null);
        originalObject = currentObjectGroup.leftMostLeaf();
        fillPlanes(planesFilled);
        panel3D.initializeModel(currentObjectGroup, false);        

        owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        
        if (showCut)
            updateCutPanel();
    }    

    /**
     * remove all line extensions
     */
    public void shortenLines()
    {   
        if ((mouseMode != INERT)
            )

        {   
        	// untoggle other buttons
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
            cutObjectGroup = null;                
            // slider weg                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                    
            panel3D.hideHelpPoint();
            DrawConstants.TICKSVISIBLE = false;            
            // this aborts other active mouse modes
            panel3D.initializeModel(currentObjectGroup, false);                    
            mouseMode = INERT;            
        }
        // find the construction
        if (currentObjectGroup instanceof ObjectWithLine)
           construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
        else if (currentObjectGroup instanceof ObjectWithPlane)
           construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
        // new lengthen factor
        DrawConstants.llFactor = 0;
        // rebuild
        currentObjectGroup = rebuild(originalObject, construction, null);
        originalObject = currentObjectGroup.leftMostLeaf();
        fillPlanes(planesFilled);
        panel3D.initializeModel(currentObjectGroup, false);        

        owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        if (showCut)
            updateCutPanel();
    }    
    
    /**
     * drawing a plane in stages:<br>
     * stepNum == 0: terminate other active mouse modes, make object-copies and wait 
     * for user to select the first point of the plane<br>
     * stepNum == 1: process the first point of the plane and wait for user to select the second
     * point of the plane<br>
     * stepNum == 2: process the second point of the plane and wait for user to select the third 
     * point of the plane<br>
     * stepNum == 3 process the third point of the plane and add this plane to currentObjectGroup
     * (that is, cut currentObjectGroup with this plane, see class ObjectWithPlane)  
     * @param stepNum the step number (0,1,2 or 3)
     * @param b if false and stepNum == 0 abort 
     */
    public void drawPlane(int stepNum, boolean b)
    {    
        // button was pressed, no points chosen
        if (stepNum == 0)
        {
            // untoggle other relevant buttons
        	if ((mouseMode != INERT) && 
                ((mouseMode != DRAWPLANE) || 
                 (DoorzienGWT.version == DoorzienGWT.EPN))
                ) 
            {               	
            	owner.topToolBar.drawLineButton.setDown(false);
                owner.topToolBar.deleteLineButton.setDown(false);
                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
                
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
                cutObjectGroup = null;

                // slider weg                                
                setSlider(false, 0, 0, 0);
                DrawConstants.TICKSVISIBLE = false;
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                // this aborts other current mouse actions
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // abort DRAWPLANE
            if (!b)
            {   
                mouseMode = INERT;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                panel3D.initializeModel(currentObjectGroup, false);
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                return;
            }
            
            mouseMode = DRAWPLANE;
            DrawConstants.TICKSVISIBLE = true;  
            // copy current to temp and put temp in Panel3D
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
            
            owner.helpBar.setText(DoorzienGWT.rb.vlakPunt1Tekst());
            // now wait for mouse action
        }
        // one point indicated/choosen, process this
        else if (stepNum == 1)
        {   
        	// for preview, not used
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
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
                panel3D.setPreviewModel(tempObjectGroup2);                

                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // for preview, not used
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
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

                panel3D.setPreviewModel(tempObjectGroup2);
                // reset
                movedPoint = null;
                movedEdgeWithPoint = null;
                return;
            }
            // at clicking one of the moved should be != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
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
            
            owner.helpBar.setText(DoorzienGWT.rb.vlakPunt2Tekst());            
            // now wait again for mouse action
        }    
        // two points choosen, process these
        else if (stepNum == 2)
        {   
            // for preview, not used
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
                // twice the same point
                if (Vector3D.equals(point1, movedPoint))
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }    
                boolean hasLine = false;
                // line through the two points could be in currentObjectGroup
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {   hasLine = true;
                }   
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsLine(
                        new Line3D(point1, movedPoint))
                   )
                {   hasLine = true;
                }   
                if (hasLine)                
                {   // tempObjectGroup is ObjectWithPoint
                    tempObjectGroup2 = (ObjectGroup3D) tempObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                    
                    // replace with second point added
                    tempObjectGroup2 = 
                        new ObjectWithPoint(tempObjectGroup2, movedPoint, 
                        		DrawConstants.planeOutlineColorIndex);
                }
                else
                {
                    // add line to copy of original
                    tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                    tempObjectGroup2 = 
                        new ObjectWithLine(tempObjectGroup2, point1, movedPoint, 
                        		DrawConstants.planeOutlineColorIndex, 0);
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
            // for preview, not used
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   

            	// note: if the first point was a point on an edge
            	// this is now a vertex
                boolean hasLine = false;
/*        
this causes sometimes a stack overflow error
probably when the chosen edge point is too close
to point1 
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
                    tempObjectGroup2 = (ObjectGroup3D) currentObjectGroup.deepCopy();
                    tempOrigObject2 = tempObjectGroup2.leftMostLeaf();
                    tempObjectGroup2 = 
                        new ObjectWithLine(tempObjectGroup2, point1, 
                            movedEdgeWithPoint[2], 
                            DrawConstants.planeOutlineColorIndex, 0);
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
            // at clicking one of the moved should be != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point1,xMoved, yMoved,
                    DrawConstants.planeOutlineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            // check here for type of point AND if this was not 
            // equal to the first
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
this causes sometimes a stack overflow error
probably when the chosen edge point is too close
to point1 
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
            }            
            
            owner.helpBar.setText(DoorzienGWT.rb.vlakPunt3Tekst());            
        }
        // third point choosen
        else if (stepNum == 3)
        {
        	// for preview, not used
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
                if (Line3D.areCollinear(point1, point2, movedPoint))
                {   // message?
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedPoint))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   
             // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedPoint))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
                // reset
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
            // for preview, not used
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
                // 3 collinear points    
                if (Line3D.areCollinear(point1, point2, movedEdgeWithPoint[2]))                
                {   // message?
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedEdgeWithPoint[2]))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, movedEdgeWithPoint[2]))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   

                // reset
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
            // at clicking one of the moved should be != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                panel3D.showHelpLine(point2,xMoved, yMoved,
                    DrawConstants.planeOutlineColor);
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            // third point chosen is a vertex     
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
                if (Line3D.areCollinear(point1, point2, clickedPoint))
                {   // message?
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return; // i.e. wait for mouse action
                }
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedPoint))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedPoint))
                   )
                {
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
                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                                
                panel3D.initializeModel(currentObjectGroup, false);        
                   
            }
            // third point chosen is a point on an edge 
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
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedEdgeWithPoint[2]))
                   )
                {
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                // plane cannot be added to currentObjectGroup twice
                if ((currentObjectGroup instanceof ObjectWithPlane) &&
                    ((ObjectWithPlane) currentObjectGroup).containsPlane(
                        new Plane3D(point1, point2, clickedEdgeWithPoint[2]))
                   )
                {
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
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                    
                panel3D.initializeModel(currentObjectGroup, false);        

            }
            addToHistory();
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            
            tempObjectGroup = null;            
            tempObjectGroup2 = null;                        
            setNumPlanes(numPlanes + 1);
            if (showCut)
                updateCutPanel();
            // for FI: stay in tool
            if (DoorzienGWT.version == DoorzienGWT.FI)
                drawPlane(0, true);                
            // for EPN out of tool    
            else if (DoorzienGWT.version == DoorzienGWT.EPN)
            {   mouseMode = INERT;
                owner.topToolBar.drawPlaneButton.setDown(false);                            
            }
        }
        
    } // drawPlane    

    
    /**
     * draw a plane parallel to a given plane in two steps:<br>
     * step 0: terminate other active mouse modes, make object-copies and wait 
     * for user to choose the plane to which the new plane should be parallel;
     * if the object contains only one plane, proceed to step 1; <br>
     * step 1: ask user for a point through which the new plane should pass, then
     * cut the object with this new plane 
     * @param stepNum the step number (0 or 1)
     * @param b abort if stepNum == 0 and false
     */
    public void drawParPlane(int stepNum, boolean b)
    {   
        boolean wasReplaced = false;
        
        // button pressed, plane should be choosen
        if (stepNum == 0)
        {
        	// untoggle other relevant buttons
            if (mouseMode != INERT)
            {   
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);
                
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                
            	
            	
                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
                cutObjectGroup = null;
                
                // remove slider                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();

                DrawConstants.TICKSVISIBLE = false;                
                //this aborts other active mouse modes                                 
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // abort DRAWPARPLANE
            if (!b)
            {   
                mouseMode = INERT;
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());                
                return;
            }
            
            mouseMode = DRAWPARPLANE;
            DrawConstants.TICKSVISIBLE = true;        
                
            // create construction 
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();

            // object contains more then one plane
            // if all these planes are parallel, no plane
            // needs to be choosen
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    
                    owner.helpBar.setText(DoorzienGWT.rb.kiesParVlakPuntTekst());                                
                    // wait for mouse action
                    // choosing the point

                }
                else
                {
                    // get the plane via the mouse
              	
                    owner.helpBar.setText(DoorzienGWT.rb.kiesParVlakTekst());                                        
                    parPlaneChoosen = null;
                    parPointChoosen = null;
                    panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                    // wait for mouse action
                    // choosing the plane
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
                movedPoint = null;
                movedEdgeWithPoint = null;
                clickedPoint = null;
                clickedEdgeWithPoint = null;
                
                owner.helpBar.setText(DoorzienGWT.rb.kiesParVlakPuntTekst());                            
                // wait for mouse action
                // choosing the point

            }
        }
        else if (stepNum == 1)
        {               
            // for preview, not used
            if ((movedPoint != null) && 
                (clickedPoint == null) &&
                (clickedEdgeWithPoint == null)
                )
            {
                // plane already there
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(movedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, movedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, movedPoint)))
                   )
                {
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;    
                }   
                
                // reset
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
            // for preview, not used
            else if ((movedEdgeWithPoint != null) && 
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)
                     )
            {   
                // plane already there
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(movedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, movedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, movedEdgeWithPoint[2])))
                   )
                {
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    return;
                }   

                // reset
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
            // at clicking one of the moved should be  != null
            else if ((movedPoint == null) && 
                     (movedEdgeWithPoint == null) &&
                     (clickedPoint == null) && 
                     (clickedEdgeWithPoint == null)            
                    )
            {   
                // reset if necessary
                if (panel3D.previewModel != null)
                    panel3D.setPreviewModel(null);        
                if (!previewOn)
                {
                    panel3D.showHelpPoint(xMoved, yMoved,
                        DrawConstants.planeOutlineColor);
                }    
                    
                return;

            }    
            // vertex choosen    
            else if (clickedPoint != null)
            {   
                // plane already there
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(clickedPoint,
                            Vector3D.plus(parPlaneChoosen.direction1, clickedPoint),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedPoint)))
                   )
                {
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;
                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;    
                }   
                parPointChoosen = new Vector3D(clickedPoint);   
                // now the plane can be added

// for an unknown reason at this point the structure 
// of currentObjectGroup is not correct and the 
// cut algorithm does not work correctly                
// however, after a deep copy, there is no problem

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
                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;                
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();

                fillPlanes(planesFilled);            
                DrawConstants.TICKSVISIBLE = false;                                
                               
                panel3D.initializeModel(currentObjectGroup, false);        
                   
            }
            // point on edge choosen
            else if (clickedEdgeWithPoint != null)
            {
            	// note: if the first or second point was a point on an edge
            	// these are now a vertices
                // plane already there
                if ((currentObjectGroup instanceof ObjectWithLine) &&
                    ((ObjectWithLine) currentObjectGroup).containsPlane(
                        new Plane3D(clickedEdgeWithPoint[2],
                            Vector3D.plus(parPlaneChoosen.direction1, clickedEdgeWithPoint[2]),
                            Vector3D.plus(parPlaneChoosen.direction2, clickedEdgeWithPoint[2])))
                   )
                {
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
                    movedPoint = null;
                    movedEdgeWithPoint = null;

                    clickedPoint = null;
                    clickedEdgeWithPoint = null;
                    return;
                }   
                parPointChoosen = new Vector3D(clickedEdgeWithPoint[2]);   
                
// see note above, make a deep copy
                
                currentObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();                
                originalObject = currentObjectGroup.leftMostLeaf();
                
                // now the plane can be added
                currentObjectGroup = new ObjectWithPlane(currentObjectGroup,
                    parPointChoosen,
                    Vector3D.plus(parPlaneChoosen.direction1, parPointChoosen),
                    Vector3D.plus(parPlaneChoosen.direction2, parPointChoosen),
                    DrawConstants.planeOutlineColorIndex, true);

                wasReplaced = false;
                if (currentObjectGroup.objects.size() > 1)
                	wasReplaced = true;

                // remains unchanged                       
                // originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.previewModel = null;
                panel3D.hideHelpLine();
                panel3D.hideHelpPoint();
                fillPlanes(planesFilled);
                DrawConstants.TICKSVISIBLE = false;                    
                              
                panel3D.initializeModel(currentObjectGroup, false);        

            }

            // a new plane was added
            if (wasReplaced)
            {
                setNumPlanes(numPlanes + 1);
                if (showCut)
                    updateCutPanel();
                addToHistory();
            }
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            
            tempObjectGroup = null;            
            tempObjectGroup2 = null;                        
            mouseMode = INERT;
            owner.topToolBar.parPlaneButton.setDown(false);                                            
        }
    }    

    /**
     * deleting a plane in stages:<br>
     * stepNum == 0: terminate other active mouse modes, if there is only one plane,
     * continue to stepNum == 1, otherwise wait for the user to select the plane to be deleted<br>
     * stepNum == 1: delete the indicated plane from currentObjectGroup using rebuild<br>
     * @param stepNum the step number (0 or 1)
     * @param b if false and stepNum == 0 abort 
     */
    public void deletePlane(int stepNum, boolean b)
    {   
    	// button pressed, plane should be choosen
        if (stepNum == 0)
        {
        	// untoggle other relevant buttons
        	if (mouseMode != INERT)
            {   
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
                cutObjectGroup = null;
                
                // remove slider weg                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                DrawConstants.TICKSVISIBLE = false;                    
                //this aborts other active mouse modes                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
        	// abort
            if (!b)
            {   mouseMode = INERT;
            	owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                return;
            }
            
            mouseMode = DELETEPLANE;
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
                        
            if (numPlanes > 1)
            {   // get the plane via the mouse
                owner.helpBar.setText(DoorzienGWT.rb.verwijderVlakTekst());            	
                planeChoosen = null;
                panel3D.helpPointColor = DrawConstants.planeOutlineColor;
                // wait for mouse action
                
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
            panel3D.hideHelpPoint();
            
            // rebuild
            construction.removeElement(planeChoosen);
            currentObjectGroup = rebuild(originalObject, construction, null);
            originalObject = currentObjectGroup.leftMostLeaf();
            fillPlanes(planesFilled);            
            panel3D.initializeModel(currentObjectGroup, false);        
            addToHistory();
            mouseMode = INERT;
            
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
            
            setNumPlanes(numPlanes - 1);        
            if (showCut)
            {   if (planeChoosen.equals(cutPlane))
                    killCutPanel();
                else
                    updateCutPanel();
                
            }
        }
    }    

    /**
     * make all planes in all relevant objects opaque (true) or transparent 
     * @param b true/false
     */
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

    }
    
    /**
     * show the intersection of the object with a chosen plane as a flat object
     * in a separate viewer; there are two steps<br>
     * step 0: terminate other active mouse modes, make object-copies and wait
     * for user to chose the plane whose cut should be shown; if the object
     * contains only one plane, proceed to step 1; <br>
     * step 1: construct the cut by the chosen plane and show it in a separate viewer
     * @param stepNum the step number (0-1) 
     * @param b abort if stepNum == 0 and false
     */
    public void showCut(int stepNum, boolean b)
    {   
    	// button pressed, plane should be chosen
        if (stepNum == 0)
        {
        	
            // untoggle other relevant buttons                    
            if ((mouseMode != INERT)) 
            {   
                owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                
                owner.topToolBar.cutButton.setDown(false);                                
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
                cutObjectGroup = null;
                // remove slider                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                DrawConstants.TICKSVISIBLE = false;                    
                //this aborts other active mouse modes                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // abort
            if (!b)
            {   showCut = false;
                mouseMode = INERT;
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                setCutPanel(showCut);
                return;   
            }    

            mouseMode = SHOWHIDECUT;
            
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
               
            if (numPlanes > 1)
            {
                // help message
            	owner.helpBar.setText(DoorzienGWT.rb.doorsnedeTekst());
                cutPlaneChoosen = null;
                // wait for mouse action
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
            panel3D.hideHelpPoint();
            
            showCut = b; // will be true
            
            cutPlane = cutPlaneChoosen.copy();
            setCutPanel(true);

            // this produces the cut as a flat object
            updateCutPanel();
            mouseMode = INERT;        
            owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        }
    }    

    /**
     * cut the object in two pieces along a plane indicated by the user;
     * after cutting, clicking on one of the pieces makes that piece the current object;
     * otherwise use the button the put the two pieces together again; three steps:
     * step 0: terminate other active mouse modes, make object-copies and wait
     * for user to chose the plane by which to cut the object; if the object
     * contains only one plane, proceed to step 1; <br>  
     * step 1: cut the object into two pieces and wait for mouse or button action
     * step 2: if the user 
     * @param stepNum the step number (0, 1 or 2)
     * @param b abort if stepNum == 0 and false
     */
    public void cutObject(int stepNum, boolean b)
    {   

        // button pressed, plane should be chosen
        if (stepNum == 0)
        {
        	// untoggle other relevant buttons
            if (mouseMode != INERT)
            {   
            	owner.topToolBar.drawLineButton.setDown(false);                
                owner.topToolBar.deleteLineButton.setDown(false);                                
                owner.topToolBar.drawPlaneButton.setDown(false);                                
                owner.topToolBar.parPlaneButton.setDown(false);
                owner.topToolBar.deletePlaneButton.setDown(false);
                owner.topToolBar.showCutButton.setDown(false);
                
                owner.rightToolBar.conDrawButton.setDown(false);                                                

                tempObjectGroup = null;
                tempObjectGroup2 = null;                
                foldOutObjectGroup = null;
                cutObjectGroup = null;
                
                // remove slider                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                panel3D.hideHelpLine();                        
                panel3D.hideHelpPoint();
                DrawConstants.TICKSVISIBLE = false;                    
                //this aborts other active mouse modes                                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            setCutPanel(false);
            figureCut = b;            
            // plakken
            if (!figureCut)
            {   mouseMode = INERT;
            
            	owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
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
	            }
	            else
	            {    
	            	owner.topToolBar.planesFilledButton.setDown(false);
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
                owner.helpBar.setText(DoorzienGWT.rb.snijdoorTekst());
            	
            	//GWT            	
//                owner.topToolBar.cutButton.setPressed(true);                                                            
                planeChoosen = null;
                // wait for mouse action
                //helpPoint = true;
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
            panel3D.hideHelpPoint();

            // if the user has chosen a side of the original object
            // abort
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

            // this produces the two pieces
            cutObjectGroup = ObjectWithPlane.cutObjectGroup(
                currentObjectGroup, planeChoosen);
                
            oldPlanesFilled = planesFilled;    
            fillPlanes(false);    
            
            panel3D.initializeModel(cutObjectGroup, false);
            
            owner.helpBar.setText(DoorzienGWT.rb.kiesDoorsnedeTekst());

            owner.topToolBar.disableLineButtons();            
            owner.topToolBar.disablePlaneButtons2();
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

        }
        else if (stepNum == 2) // user has clicked a facet
        {

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

            // choose left piece, translate to center of viewer
            // and apply translated (!) contruction
            if (left.containsFacet(facetChoosen) >= 0)
            {               
                ObjectWithPlane.letterObject(topLeft);
                Vector3D leftTrans = Vector3D.minus(
                    new Vector3D(0,0,0), topLeft.center);
                    
                topLeft.center();
                
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
                               
                currentObjectGroup = rebuild(topLeft, newLeftConstruction, null);
                originalObject = currentObjectGroup.leftMostLeaf();

                cutObjectGroup = null;
                addToHistory();                
                
                planesFilled = oldPlanesFilled;
                fillPlanes(planesFilled);
                if (planesFilled)
                {    owner.topToolBar.planesFilledButton.setDown(true);

                }
                else
                {    owner.topToolBar.planesFilledButton.setDown(false);
            
                }
                
                panel3D.initializeModel(currentObjectGroup, false);                                    
                mouseMode = INERT;
                
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                
                setNumLines(nLines);
                setNumPlanes(nPlanes);
                owner.rightToolBar.undoButton.setEnabled(true);                                

            }
            // choose right piece, translate to center of viewer
            // and apply translated (!) contruction
            else if (right.containsFacet(facetChoosen) >= 0)
            {   
            
                ObjectWithPlane.letterObject(topRight);
                Vector3D rightTrans = Vector3D.minus(
                    new Vector3D(0,0,0), topRight.center);
                topRight.center();    
                
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
                currentObjectGroup = rebuild(topRight, newRightConstruction, null);
                originalObject = currentObjectGroup.leftMostLeaf();
                cutObjectGroup = null;
                addToHistory();
                
                planesFilled = oldPlanesFilled;
                fillPlanes(planesFilled);
                if (planesFilled)
                {    owner.topToolBar.planesFilledButton.setDown(true);
            
                }
                else
                {    owner.topToolBar.planesFilledButton.setDown(false);
            
                }

                panel3D.initializeModel(currentObjectGroup, false);                                    
                mouseMode = INERT;
                
                owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
                
                setNumLines(nLines);
                setNumPlanes(nPlanes);
                owner.rightToolBar.undoButton.setEnabled(true);                                
            }
                                       
        }
    }

    /**
     * reset buttons, abort active mouse modes
     * and show the previous object from the history  
     */
    public void undo()
    {   
    	// untoggle other buttons
    	if (mouseMode != INERT)
        {   
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
            cutObjectGroup = null;
            // remove slider                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                        
            panel3D.hideHelpPoint();
            DrawConstants.TICKSVISIBLE = false;                        
        }    
        mouseMode = INERT;
       
        owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        
        previousObjectGroup();

        if ((numLines > 0) && (DrawConstants.llFactor > 0))
            owner.topToolBar.shortLinesButton.setEnabled(true);
        
        if (showCut)
        {   boolean hasCutPlane = false;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    hasCutPlane = ((ObjectWithLine) currentObjectGroup).containsPlane(cutPlane);
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {    hasCutPlane = ((ObjectWithPlane) currentObjectGroup).containsPlane(cutPlane);
            }
            if (!hasCutPlane)
            {    killCutPanel();
            }
            else
            {    updateCutPanel();
            }
                
        }    
        
    }    

    /**
     * reset buttons, abort active mouse modes
     * and show the next object from the history  
     */
    public void redo()
    {   // untoggle other buttons
    	if (mouseMode != INERT)
        {   
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
            cutObjectGroup = null;
            // remove slider                                
            setSlider(false, 0, 0, 0);
            panel3D.hideHelpLine();                        
            panel3D.hideHelpPoint();
            DrawConstants.TICKSVISIBLE = false;                            

        }    
        mouseMode = INERT;
        
        owner.helpBar.setText(DoorzienGWT.rb.draaiTekst());
        
        nextObjectGroup();
        if ((numLines > 0) && (DrawConstants.llFactor > 0))
            owner.topToolBar.shortLinesButton.setEnabled(true);
        
        if (showCut)
        {   boolean hasCutPlane = false;
            if (currentObjectGroup instanceof ObjectWithLine)
            {    hasCutPlane = ((ObjectWithLine) currentObjectGroup).containsPlane(cutPlane);
            }
            else if (currentObjectGroup instanceof ObjectWithPlane)
            {    hasCutPlane = ((ObjectWithPlane) currentObjectGroup).containsPlane(cutPlane);
            }
            if (!hasCutPlane)
            {    killCutPanel();
            }
            else
            {    updateCutPanel();
            }
                
        }    
        
    }    

    /**
     * a vertex of a facet was clicked; check if this vertex is allowed, that is:<br>
     * 1) all versions: the vertex cannot be a vertex of a facet that is a line extension<br>
     * 2) EPN-version: the vertex must belong to a facet (replacing part of) a facet
     * of the original 3d-object (that is the 3d-object without lines and planes);
     * thus in the EPN-version no vertices can be clicked on added lines (except
     * when this line is an edge of the original 3d-object) or on lines which are intersections
     * of a plane with an original facet or two planes (with the line not being an edge);  
     * @param fwv the facet and its vertex that was clicked
     * @return true/false
     */
    public boolean vertexAllowed(FacetWithVertex fwv)
    {
        // ESSENTIAL RESTRICTION for all versions       
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
        // RESTRICTION TO THE ORIGINAL OBJECT for the EPN-version        
        else if (DoorzienGWT.version == DoorzienGWT.EPN)
        {
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

            return isOnOrig;
        }
        return false;
    }

    /**
     * a point on an edge was clicked; check if this point is allowed, that is:<br>
     * 1) all versions: the point cannot be a point on a facet that is a line extension<br>
     * 2) EPN-version: the point must be on an edge of a facet (replacing part of) a facet
     * of the original 3d-object (that is the 3d-object without lines and planes);
     * thus in the EPN-version no points can be clicked on added lines (except
     * when this line is an edge of the original 3d-object) or on lines which are intersections
     * of a plane with an original facet or two planes (with the line not being an edge);  
     * @param fwep the edge and the point on it that was clicked
     * @return true/false
     */
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
            return isOnOrig;
        }
        return false;
    }    
    
    /**
     * make the fold out angle maximal, producing flat
     * object and display this as a flat object by rotating  
     */
    public void flattenAction()
    {
        // rotate the foldOutGroup in view space
        Vector3D from = new Vector3D(
            startFacet.unitNormal.x,
            startFacet.unitNormal.y,
            startFacet.unitNormal.z);
            
        Vector3D to = new Vector3D(0, 0, 1);
        panel3D.vwRotate(from, to);

        // put slider to 100% (maximal fold out)
        processSlider(1);
        slider.setPosition(1);
    	
    }
	
    /**
     * inner class for handling Click Events on the flatButton
     * @author huub
     */
	class FlatCL implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			flattenAction();
			flattened = true;
		}
	}
			
    /**
     * inner class handling Mouse Events of the drawing Canvas 
     * @author huub
     */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
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
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MouseHandler

    /**
     * inner class handling Touch Events of the drawing Canvas 
     * @author huub
     */
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
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
	
	/**
	 * action at MouseDown/TouchStart Event: 
	 * mouseMode == INERT start dragging the object<br>
	 * in all the following: if nothing relevant is clicked, rotate<br>  
	 * mouseMode == DRAWLINE check if a a vertex or a point on an edge
	 * was clicked (if clicking this point is allowed); this is done twice;<br> 
	 * mouseMode == DELETELINE check if an edge was clicked which is part of
	 * a line in the current object (this line will subsequently be deleted);<br> 
	 * mouseMode == DRAWPLANE check if a a vertex or a point on an edge
	 * was clicked (if clicking this point is allowed); this is done three times;<br>
	 * mouseMode == PARPLANE if no plane was choosen, check if an edge of a plane
	 * in the object was clicked and fix this plane; if a plane was choosen,
	 * check if a vertex or a point on an edge of the object was clicked;<br>
	 * mouseMode == DELETEPLANE check if an edge was clicked which is part of
	 * a plane in the current object (this plane will subsequently be deleted);<br> 
	 * mouseMode == SHOWHIDECUT if no plane was choosen, check if an edge of a plane
	 * in the object was clicked and show the cut of the object with this plane;<br>
	 * mouseMode == CUTOBJECT if no plane was choosen, check if an edge of a plane
	 * in the object was clicked and cut the object in two pieces along this plane;
	 * if a plane was choosen (and the object was cut), check if a facet of one of these
	 * pieces was clicked, and if yes, make this piece the new current object;<br>
	 * mouseMode == FOLDOUT of a start facet for the foldout was chosen, rotate (the fold out);
	 * if no startFacet was choosen, check if a facet was clicked; note: this facet must be
	 * a facet replacing a facet of the origianl object; if the facet clicked is acceptable,
	 * make the fold out;  
	 * @param eventX x-coordinate of Event
	 * @param eventY y-coordinate of Event
	 */
    public void mouseDownTouchStartAction(int eventX, int eventY)
	{

      	xClicked = eventX;
        yClicked = eventY;
            
        if (mouseMode == INERT)
        {   panel3D.oldX = eventX;
            panel3D.oldY = eventY;
            xStart = eventX;
            yStart = eventY;
            dragging = true;
                
        }    
        else if (mouseMode == DRAWLINE) 
        {   
            FacetWithVertex fwv = 
                panel3D.facetWithVertexClicked(xClicked, yClicked);
                    
            // restrictions for EPN
            // 1) only vertices of originalObject (visible or invisible)
            // 2) only vertices of tempObjectGroup which are located
            //    on originalObject, i.e. new points on edges of originalObject  
            //    can also be used
            // general restriction
            // 3) no points on extended lines
                                          
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
            {      
                FacetWithEdgePoint fwep = 
                    panel3D.facetWithEdgePointClicked(xClicked, yClicked);
                            
                // restrictions see above 
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
                    xStart = eventX;
                    yStart = eventY;
                        
                    dragging = true;                            
                }
            }
            else // nothing relevant clicked
            {   panel3D.oldX = eventX;
                panel3D.oldY = eventY;
                xStart = eventX;
                yStart = eventY;
                   
                dragging = true;                            
            }    
            
        }
        else if (mouseMode == DRAWPLANE) 
        {   
            FacetWithVertex fwv = 
                panel3D.facetWithVertexClicked(xClicked, yClicked);
                
            // restrictions for EPN
            // 1) only vertices of originalObject (visible or invisible)
            // 2) only vertices of tempObjectGroup which are located
            //    on originalObject, i.e. new points on edges of originalObject  
            //    can also be used
            // general restriction
            // 3) no points on extended lines
            if (fwv != null)
            {
                if (vertexAllowed(fwv))    
                {
                    // process this point
                    clickedPoint = fwv.vertex;
                    drawPlane(pointsSelected + 1, true);
                }
                    
            }    
            else // no vertex clicked, check for an edge
            {                           

                FacetWithEdgePoint fwep = 
                    panel3D.facetWithEdgePointClicked(xClicked, yClicked);
                // restrictions see above
                if (fwep != null)
                {   
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
                    xStart = eventX;
                    yStart = eventY;
                        
                    dragging = true;                            
                }
            }    
        } // mouseMode == DRAWPLANE
            
        else if (mouseMode == DRAWPARPLANE)
        {
         	// plane choosen, so get the point
            if (parPlaneChoosen != null)
            {   
                FacetWithVertex fwv = 
                    panel3D.facetWithVertexClicked(xClicked, yClicked);
                // restrictions for EPN
                // 1) only vertices of originalObject (visible or invisible)
                // 2) only vertices of tempObjectGroup which are located
                //    on originalObject, i.e. new points on edges of originalObject  
                //    can also be used
                // general restriction
                // 3) no points on extended lines
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
                {   
                    FacetWithEdgePoint fwep = 
                        panel3D.facetWithEdgePointClicked(xClicked, yClicked);
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
                        xStart = eventX;
                        yStart = eventY;
                        
                        dragging = true;                            
                    }
                }    
            }
            else // parPlaneChoosen == null, so get the plane
            {
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
                             
                        owner.helpBar.setText(DoorzienGWT.rb.kiesParVlakPuntTekst());                                                                
                        // wait for mouse action choosing the point

                    }
                    else // rotate
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
                        xStart = eventX;
                        yStart = eventY;
                            
                        dragging = true;                            
                    }
                }    
                else // nothing relevant clicked
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
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
                    xStart = eventX;
                    yStart = eventY;
                        
                    dragging = true;                            
                }
            }
            else // nothing relevant clicked
            {   panel3D.oldX = eventX;
                panel3D.oldY = eventY;
                xStart = eventX;
                yStart = eventY;
                    
                dragging = true;                            
            }    
            
        }
            
        else if (mouseMode == SHOWHIDECUT)
        {   if (cutPlaneChoosen != null)
            {   panel3D.oldX = eventX;
                panel3D.oldY = eventY;
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
                            }    
                        }
                    }
                    if (cutPlaneChoosen != null)
                        showCut(1, true);
                    else // rotate
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
                        xStart = eventX;
                        yStart = eventY;
                            
                        dragging = true;                            
                    }
                }    
                else // nothing relevant clicked
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
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
                            }    
                        }
                    }
                    if (planeChoosen != null)
                        cutObject(1, true);
                    else // rotate
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
                        xStart = eventX;
                        yStart = eventY;
                            
                        dragging = true;                            
                    }
                }    
                else // nothing relevant clicked
                {   panel3D.oldX = eventX;
                    panel3D.oldY = eventY;
                    xStart = eventX;
                    yStart = eventY;
                        
                    dragging = true;                            
                }    
            } // planeChoosen == null
        }
            
        else if (mouseMode == FOLDOUT)
        {   // startFacet choosen: rotate
            if (startFacet != null)
            {   panel3D.oldX = eventX;
                panel3D.oldY = eventY;
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
                    else 
                    {   startFacet = temp;
                    }    
                    // facet does not make sense
                    if (startFacet == null)
                    {   panel3D.oldX = eventX;
                        panel3D.oldY = eventY;
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
                    xStart = eventX;
                    yStart = eventY;
                        
                    dragging = true;                            
                }
            }
            
        }

	}
	
	/**
	 * dragg action action at MouseMove/TouchMove Event
	 * two types of dragg actions:<br>
	 * 1) dragg in a circle around the center of the screen: the usual dragging,
	 * that is, object rotates around an axis perpendicular to the dragg
	 * direction <br>
	 * 2) dragg outside a circle around the center of the screen: object rotates around
	 * the screen-z-axis
	 * @param eventX x-coordinate of Event
	 * @param eventY y-coordinate of Event
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
       	if (dragging)
        {

            int xCenter = panel3D.breedte / 2; 
            int yCenter = panel3D.hoogte / 2; 
            int minRad = Math.min(xCenter, yCenter);
            
            inCircle = Math.sqrt((xStart - xCenter) * (xStart - xCenter) +
                                 (yStart - yCenter) * (yStart - yCenter)) < minRad * RADFACTOR;

            // dragg outside of drawing area
            if ((eventX <= 0) || (eventY <= 0) || (eventX >= panel3D.breedte) ||
                (eventY >= panel3D.hoogte))
            {    
                dragging = false;
                return;    
            }
                
            if (inCircle)
            {
                    
                double xTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.breedte; //getSize().width;
                double yTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.hoogte; //getSize().height;
                                
                panel3D.rotateBy(xTheta, yTheta);
                panel3D.repaint();
                
                panel3D.oldX = eventX; 
                panel3D.oldY = eventY; 
            }
            else // not inCircle
            {
                double centerX = ((double) panel3D.breedte) / 2; 
                double centerY = ((double) panel3D.hoogte) / 2;                     
                double xTheta = 0;
                double yTheta = 0;

                // choose correct direction
                if (eventX < centerX)
                    yTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.hoogte; 
                else                    
                    yTheta = (eventY - panel3D.oldY) * 180.0d / panel3D.hoogte; 
                if (eventY < centerY)             
                    xTheta = (eventX - panel3D.oldX) * 180.0d / panel3D.breedte; 
                else                 
                    xTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.breedte;
                                     
                double zTheta = 0;
                    
                if (Math.abs(yTheta) > Math.abs(xTheta))
                    zTheta = yTheta;
                else
                    zTheta = xTheta;
                    
                panel3D.rotateByZ(zTheta);
                panel3D.repaint();
                
                panel3D.oldX = eventX; 
                panel3D.oldY = eventY; 
                    
            }    
        } // if (dragging)
        else
        {   
           dragging = false;
        }    

	}

	/**
	 * action at MouseUp/TouchEnd Event: repaint and end dragging 
	 */
	public void mouseUpTouchEndAction()
	{
		if (dragging)
        {
            // make sure the dragg-event-queue for rotating is completed!!
            panel3D.repaint();
            dragging = false;
        }
	}
  

	/**
	 * inner class handling Mouse Events of the Canvas in the cutPanel; in this case dragging
	 * rotates the cut in the screen plane 
	 * @author huub
	 */
	class CutMouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		boolean dragging = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			// prevent scrolling 
			e.stopPropagation();
			
			mouseDown = true;
			
    		cutPanel.oldX = e.getX();
            cutPanel.oldY = e.getY();
            dragging = true;
            
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
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
                cutPanel.repaint();
                cutPanel.oldX = e.getX();
                cutPanel.oldY = e.getY();
            }
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
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

	} // CutMouseHandler

	/**
	 * inner class handling Touch Events of the Canvas in the cutPanel; in this case dragging
	 * rotates the cut in the screen plane  
	 * @author huub
	 */
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

	} // // CutTouchHandler
	
} // class DrawingPanel2

