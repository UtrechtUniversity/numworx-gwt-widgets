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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchCancelHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

//class for main drawing area
//a Panel containing one or more objects to be drawn in
//such as an Object3DContainer or others
//also contains all control routines
public class DrawingShell //extends LayoutPanel
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
//  int modelCode = 0;
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
//  public static double llFactor = 0;

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
//  double oldZoom;

    
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
//  public DropButton dropButton;
//  public LWPopUp2 dropMenu;
  
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
  
//for testing
String testString = "";
//font for testing
//Font fo = new Font("Helvetica", Font.PLAIN, 11);
//GWT
//FontMetrics fm = getFontMetrics(fo);


	Canvas drawingPanelCanvas;
	Context2d drawingPanelContext2d;
	int breedte, hoogte;
	
	boolean dragging = false;
  boolean inCircle = false;
  int xStart, yStart;
	
//for testing speed

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
  public DrawingShell(DoorzienGWT o, int b, int h, int startModel)
  {   owner = o;
  
  	breedte = b;
  	hoogte = h;
  
  	drawingPanelCanvas = Canvas.createIfSupported();
  	drawingPanelCanvas.setWidth(breedte + "px");
  	drawingPanelCanvas.setHeight(hoogte + "px");
  	drawingPanelCanvas.setCoordinateSpaceWidth(breedte);
  	drawingPanelCanvas.setCoordinateSpaceHeight(hoogte);

  	drawingPanelCanvas.addStyleName(owner.doorzienGWTCss.canvas());    	
  	
  	MouseHandler mouseHandler = new MouseHandler();
  	drawingPanelCanvas.addMouseDownHandler(mouseHandler);
  	drawingPanelCanvas.addMouseMoveHandler(mouseHandler);
  	drawingPanelCanvas.addMouseUpHandler(mouseHandler);
  
  	TouchHandler touchHandler = new TouchHandler();
  	drawingPanelCanvas.addTouchStartHandler(touchHandler);
  	drawingPanelCanvas.addTouchMoveHandler(touchHandler);
  	drawingPanelCanvas.addTouchEndHandler(touchHandler);
  
  	drawingPanelContext2d = drawingPanelCanvas.getContext2d();
  	
  	panel3D = new Object3DContainer(drawingPanelContext2d, breedte, hoogte);
//GWT    	
  	cutPanel = new Object3DContainer(drawingPanelContext2d, breedte, hoogte);
      
//ALLEEN VOOR FI        
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
		slider = new Slider2(this, 0, 1);
		add(slider.sliderCanvas);
    	setWidgetLeftWidth(slider.sliderCanvas, breedte - slider.horSize - 1, Style.Unit.PX, slider.horSize, Style.Unit.PX);
		setWidgetTopHeight(slider.sliderCanvas, 1, Style.Unit.PX, slider.vertSize, Style.Unit.PX);
		slider.setVisible(false);
*/      
      
//      cutml = new CutMLMML();
//      cutPanel.addMouseListener(cutml);
//      cutPanel.addMouseMotionListener(cutml);        
      
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
      boolean currentVisible = 
          (panel3D.model == currentObjectGroup);
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
//voor FI nog tempObjectGroup2        
  
  }
  
  // switching on and off from the MENU
  // default on is divide in two parts
  public void setHelpPointDrop(boolean b)
  {   
  	
//GWT
/*    	
  	if (b)
      {   setHelpPoints(1);
          dropMenu.switchTo(owner.tt("twoPartsText"));
          if ((mouseMode == DRAWLINE) ||
              (mouseMode == DRAWPLANE) ||
              (mouseMode == DRAWPARPLANE)
             )
          {   panel3D.add(dropButton);
              panel3D.repaint();
          }    
      }
      else
      {   setHelpPoints(0);
          panel3D.remove(dropMenu);
          panel3D.remove(dropButton);
          panel3D.repaint();
      }    
*/    
  }
  
  // showing the drop button etc.
  public void showHelpPointDrop(boolean b)
  {   

//GWT    	
/*    	
  	if (b)
      {   if (DrawConstants.TICKNUM > 0)
          {
              if (dropButton != null)
                  panel3D.add(dropButton);
              panel3D.repaint();
          }
      }
      else
      {   if (dropMenu != null)
              panel3D.remove(dropMenu);
          if (dropButton != null)    
              panel3D.remove(dropButton);
          panel3D.repaint();
      }
*/            
  
  }
  
  // changing the model to a new one
  public void setNewModel(int modelCode)
  {   
		// algemene reset
		// boven
//      owner.topToolBar.resetDefaults();
      
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
//      owner.rightToolBar.resetDefaults();

      // enable options
//GWT        
//      owner.enableOptions(true);
      DrawConstants.letters = false;
//GWT        
//      owner.resetLetters();

		// projectie
      //if (DoorzienDWO.version == DoorzienDWO.EPN)
      //    setProjection(PARALLELPROJ);        
      //else
          setProjection(CENTRALPROJ);
//GWT            
//      owner.resetProjection(projection);
      
      // hulppunten
      DrawConstants.TICKNUM = 0;
//GWT        
//      owner.resetHelpPoints();
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
		if (modelCode < DoorzienGWT.MYFIGURE)
		{
	        currentObjectGroup = makeNewModel(modelCode);        
  	    // HIER!
	        setFilled(false);        
  	    panel3D.initializeModel(currentObjectGroup, true);

	        // reset zooming HERE
  	    zoom = defaultZoom;
      	panel3D.setZoomFactor(zoom);        
      
      	addToHistory();
      	
//      	owner.helpBar.setText(owner.tt("rotateText"));
//          owner.helpBar.setText(TextConstants.rotateText);
      }
      //else if ((modelCode == owner.MYFIGURE) && !startUp)
      //	owner.viewer.setScormedObject3D();	
      
  }    

  public ObjectGroup3D makeNewModel(int code)
  {   modelCode = code;
      Object3D model;
      ObjectGroup3D modelGroup;
      // default?
//binnenvulling is onzichtbaar
//maar voor buitenkant toch NZMINFIRST
//is dit ook OK voor filled = false?
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
//      if (history.size() > 1)
//          owner.rightToolBar.undoButton.setEnabled(true);
//System.out.println("added, his = " + history.size());                
//      owner.rightToolBar.redoButton.setEnabled(false);            
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
//      if (historyPointer == 0)
//          owner.rightToolBar.undoButton.setEnabled(false);
//      owner.rightToolBar.redoButton.setEnabled(true);            
      
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
//      if ((history.size() - 1) == historyPointer)
//          owner.rightToolBar.redoButton.setEnabled(false);
//      owner.rightToolBar.undoButton.setEnabled(true);            
      
  }    


  public void setNumLines(int nLines)
  {   numLines = nLines;
      if (nLines == 0)
      {   DrawConstants.llFactor = 0;
//          owner.topToolBar.activateLineButtons(false);
      }
      else
      {
//          owner.topToolBar.activateLineButtons(true);   
      }    

  }
  
  public void setNumPlanes(int nPlanes)
  {   numPlanes = nPlanes;
      if (nPlanes == 0)
      {   if (showCut)
              killCutPanel();
          planesFilled = false;
//          owner.topToolBar.activatePlaneButtons(false);
      }
      else
      {
//          owner.topToolBar.activatePlaneButtons(true);   
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
//dit kan ook deep copy in OWL en OWP vervangen
//wanneer die methode niet goed werkt
  }
  
  public void setSlider(boolean b, double init, double min, double max)
  {   
// 	  if (mouseMode == FOLDOUT)
//	  {
//		  currentFoldOut = init;
//	  }


	  if (slider == null)
		  return;
    	
  	if (b)
    {   sliderValue = init;
//System.out.println("initValue = " + UF.format(sliderValue, 2));            
          //slider = new Slider2(this, min, max);
      	  slider.setMinMax(min, max);
      	  slider.setPosition(sliderValue);
          if (mouseMode == FOLDOUT)
          {   currentFoldOut = sliderValue;
//          	  flatButton = new LWButton(Table.lookUp("flatText"),
//                           30, slider.getSize().height);
//              flatButton.setLocation(
//                  panel3D.getSize().width - flatButton.getSize().width,
//                  0);
             
//              panel3D.add(flatButton);
              // add listener                
//              flatButton.addMouseListener(new FlatML());
              flattened = false;
//              slider.setLocation(
//                  panel3D.getSize().width - slider.getSize().width -
//                      flatButton.getSize().width,
//                  0);    
              
          }    
          else    
          {   
        	  //slider.setLocation(
              //   panel3D.getSize().width - slider.getSize().width,
              //    0);

          }        
//          panel3D.add(slider);    
//          panel3D.repaint();
          
      }
      else
      {   
//    	  if (slider != null)
//              panel3D.remove(slider); 
//          slider = null;    
//          if (flatButton != null)
//              panel3D.remove(flatButton); 
//          flatButton = null;    
      }
      slider.setVisible(b);
      flatButton.setVisible(b);
  }
  

  public void processSlider(double newValue)
  {   sliderValue = newValue;
  
//System.out.println("newValue = " + UF.format(newValue, 2));    
      if (mouseMode == FOLDOUT)
      {	  currentFoldOut = sliderValue;
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
  {   // add
  	
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
//      owner.topToolBar.showCutButton.setImage(owner.showCutImage);
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

  public void zoomIn(double zoomStep)
  {   double temp = zoom + zoomStep;
      if (temp <= (MAXZOOM + zoomStep / 10))
      {   zoom = temp;
          panel3D.setZoomFactor(zoom);
          // cutPanel            
          if (showCut)
              cutPanel.setZoomFactor(zoom);
      }    
  }
  
  public void zoomOut(double zoomStep)
  {   double temp = zoom - zoomStep;
      if (temp >= (MINZOOM - zoomStep / 10))
      {   zoom = temp;
          panel3D.setZoomFactor(zoom);
          // cutPanel            
          if (showCut)
              cutPanel.setZoomFactor(zoom);
          
      }    

  }

  public void makeFoldOut(int stepNum, boolean b)
  {   // facet must be choosen
      // pressing the button
      if (stepNum == 0)
      {   
          // hier of andere relevante knoppen "doof maken"
          // of als je wat anders aanklikt drawline aborteren
          // zo kun je ook drawLine opnieuw starten
          if (mouseMode != INERT)
          {   // zet model gelijk aan current
              // dit aborteert andere LOPENDE muis acties
          	
//GWT
/*            	
              owner.topToolBar.drawLineButton.setPressed(false);                
              owner.topToolBar.deleteLineButton.setPressed(false);                                
              owner.topToolBar.drawPlaneButton.setPressed(false);                                
              owner.topToolBar.parPlaneButton.setPressed(false);
              owner.topToolBar.deletePlaneButton.setPressed(false);
              owner.topToolBar.showCutButton.setPressed(false);                
              owner.topToolBar.cutButton.setPressed(false);                                
              owner.rightToolBar.conDrawButton.setPressed(false);                                                
*/                
              tempObjectGroup = null;
              tempObjectGroup2 = null;
              foldOutObjectGroup = null;
              cutObjectGroup = null;                
              if (numPlanes > 0)
              {   
//GWT                	
//              	owner.topToolBar.cutButton.setImage(owner.cutImage);
              
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
          {   mouseMode = INERT;
              setCutPanel(showCut);
              
//              owner.helpBar.setText(TextConstants.rotateText);
//GWT                
//              owner.helpBar.setText(owner.tt("rotateText"));                    
//              owner.enableOptions(true);
//if (historyPointer > 0)
//owner.rightToolBar.undoButton.setEnabled(true);
//if (historyPointer < (history.size() - 1))
//owner.rightToolBar.redoButton.setEnabled(true);
              setNumLines(numLines);
              setNumPlanes(numPlanes);
              filled = oldFilled;
              setFilled(filled);
              if (filled)
              {    
//GWT                	
//              	owner.rightToolBar.wireSolidButton.setImage(owner.wireFrameImage);
              
              }
              else
              {    
//GWT                	
//              	owner.rightToolBar.wireSolidButton.setImage(owner.solidImage);
              
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
//GWT
//          owner.enableOptions(false);      
          
          foldOutObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
          foldOutObject = foldOutObjectGroup.leftMostLeaf(); 
          
          foldOutObject.loosenVertices();

          //startFacet = null;
          panel3D.initializeModel(foldOutObjectGroup, false);
          
          if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
              (foldOutObject.modelCode == DoorzienGWT.CYLINDER))
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
                   ((foldOutObject.modelCode == DoorzienGWT.CONE1) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE2) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE3) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE4)
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
//GWT            	
//              owner.helpBar.setText(owner.tt("conDrawSelectText"));
//              owner.rightToolBar.conDrawButton.setPressed(true);
              // wait for mouse action
              helpPoint = true;
              panel3D.helpPointColor = DrawConstants.planeOutlineColor;
          }
          
      }     
      else if (stepNum == 1)
      {   
//GWT        	
//      	owner.rightToolBar.conDrawButton.setPressed(false);
          foldOutObjectGroup.setTickMarks(0);

          helpPoint = false;
          panel3D.hideHelpPoint();

//GWT            
//          owner.enableOptions(false);
          
 //         owner.topToolBar.disableLineButtons();
//          owner.topToolBar.disablePlaneButtons();
//owner.rightToolBar.undoButton.setEnabled(false);
//owner.rightToolBar.redoButton.setEnabled(false);
          
			// asumed >= 0    
          //int startIndex = foldOutObject.containsFacet(startFacet);    
			int startIndex = NoSer.containsFacet(foldOutObject, startFacet);
          
          
          // init facet labels
          facetsUsed = new boolean[foldOutObject.numFacets]; 
          // create root node, mark facet as labeled
          foldOutTreeRoot = new FoldOutTreeNode(startFacet, 0, 0, 0);
          facetsUsed[startIndex] = true;
          
          if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
              (foldOutObject.modelCode == DoorzienGWT.CYLINDER))
              addTreeCylinderNode(foldOutTreeRoot);            
          else if ((DoorzienGWT.version == DoorzienGWT.EPN) && 
                   ((foldOutObject.modelCode == DoorzienGWT.CONE1) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE2) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE3) ||
                    (foldOutObject.modelCode == DoorzienGWT.CONE4)
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
      
//      Vector nodes = new Vector();
//      enumTree(foldOutTreeRoot, nodes, 0);
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
          
//GWT            
//          owner.rightToolBar.wireSolidButton.setImage(owner.wireFrameImage);
          setSlider(true, foldOutInit, 0, 1);
          
//          owner.helpBar.setText(TextConstants.rotateText);
          
//GWT            
//          owner.helpBar.setText(owner.tt("rotateText"));        
//          owner.rightToolBar.conDrawButton.setImage(owner.figureImage);
      } // else if (stepNum == 1)
  }
  
  
  
/*    
//tijdelijk
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
//                  Line3D rotAxis = new Line3D(edgeStart, edgeEnd);
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
//always add startFacet since this contains
//the axisdata
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
//                 UF.format(rotAngle * 360 / (2 * Math.PI), 12));
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
//                  fa.setNormal();
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
  
//optie hulppunten:
//via een rebuild, 3 modes possible!
  
  
//bij andere knoppen waar nodig kijken of mouseMode == INERT    
  
  

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

  public void cutObject(int stepNum, boolean b)
  {   // als meer vlakken kies een vlak
      // gebruik mousemode
      if (stepNum == 0)
      {
          if (mouseMode != INERT)
          {   // zet model gelijk aan current
              // dit aborteert andere LOPENDE muis acties
//GWT
/*            	
              owner.topToolBar.drawLineButton.setPressed(false);                
              owner.topToolBar.deleteLineButton.setPressed(false);                                
              owner.topToolBar.drawPlaneButton.setPressed(false);                                
              owner.topToolBar.parPlaneButton.setPressed(false);
              owner.topToolBar.deletePlaneButton.setPressed(false);
              owner.topToolBar.showCutButton.setPressed(false);                
              owner.topToolBar.cutButton.setPressed(false);                                
              owner.rightToolBar.conDrawButton.setPressed(false);                                                
*/                
              tempObjectGroup = null;
              tempObjectGroup2 = null;                
              foldOutObjectGroup = null;
//GWT                
//              owner.rightToolBar.conDrawButton.setImage(owner.conDrawImage);                
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
          
//          	owner.helpBar.setText(TextConstants.rotateText);
//GWT            
//              owner.enableOptions(true);
              setCutPanel(showCut);
              setNumLines(numLines);
              setNumPlanes(numPlanes);
              
	            planesFilled = oldPlanesFilled;
  	        fillPlanes(planesFilled);
	            if (planesFilled)
	            {
//	            	owner.topToolBar.planesFilledButton.setDown(true);
//	            	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
	            
	            }
	            else
	            {    
//	            	owner.topToolBar.planesFilledButton.setDown(false);
//	            	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
	            
	            }
      
      		if (historyPointer > 0)
      		{   
//      			owner.rightToolBar.undoButton.setEnabled(true);                
      		}
//  	        owner.rightToolBar.redoButton.setEnabled(false);                
  	            
//GWT    	        
//              owner.topToolBar.cutButton.setPressed(false);                                            
              panel3D.testString = "";
              //addToHistory();
              
              return;   
          }    
          mouseMode = CUTOBJECT;
//GWT            
//          owner.enableOptions(false);
          if (currentObjectGroup instanceof ObjectWithLine)
              construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
          else if (currentObjectGroup instanceof ObjectWithPlane)
              construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
          if (numPlanes > 1)
          {
              // help message
//              owner.helpBar.setText(TextConstants.selectCutPlaneText);
          	
          	//GWT            	
//              owner.topToolBar.cutButton.setPressed(true);                                                            
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
//          owner.topToolBar.cutButton.setPressed(false);                                                        
          panel3D.hideHelpPoint();
          helpPoint = false;
//hier zijvlak onderscheppen
//via return en alles terugzetten
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
          
//          owner.helpBar.setText(TextConstants.selectCutFigureText);

//          owner.topToolBar.disableLineButtons();            
//          owner.topToolBar.disablePlaneButtons2();
          //owner.topToolBar.cutButton.setEnabled(true);
//GWT            
//          owner.topToolBar.cutButton.setImage(owner.glueImage);                
//          owner.rightToolBar.undoButton.setEnabled(false);
//          owner.rightToolBar.redoButton.setEnabled(false);
          
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
          {    
//        	  owner.topToolBar.planesFilledButton.setDown(true);
//          	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
          
          }
          else
          {    
//        	  owner.topToolBar.planesFilledButton.setDown(false);
//          	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
          
          }
              
panel3D.testString = "";                
              panel3D.initializeModel(currentObjectGroup, false);                                    
              mouseMode = INERT;
              
//              owner.helpBar.setText(TextConstants.rotateText);
              
              setNumLines(nLines);
              setNumPlanes(nPlanes);
//              owner.rightToolBar.undoButton.setEnabled(true);                                
              if (numPlanes > 0)                
              {    
//GWT                	
//              	owner.topToolBar.cutButton.setImage(owner.cutImage);
              
              }
//GWT                
//              owner.enableOptions(true);                        
//              owner.resetHelpPoints(); 
              setHelpPointDrop(false);                
//System.out.println("left");                            

//numPlanes numLines

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
          {    
        	  
//        	  owner.topToolBar.planesFilledButton.setDown(true);
          
//          	owner.topToolBar.planesFilledButton.setImage(owner.planesEmptyImage);
          
          }
          else
          {    
//        	  owner.topToolBar.planesFilledButton.setDown(false);
//          	owner.topToolBar.planesFilledButton.setImage(owner.planesFilledImage);
          
          }

              
panel3D.testString = "";                                
              //currentObjectGroup = right;
              //originalObject = topRight;
              panel3D.initializeModel(currentObjectGroup, false);                                    
              mouseMode = INERT;
              
//              owner.helpBar.setText(owner.tt("rotateText"));
//              owner.helpBar.setText(TextConstants.rotateText);
              
              setNumLines(nLines);
              setNumPlanes(nPlanes);
//              owner.rightToolBar.undoButton.setEnabled(true);                                
              if (numPlanes > 0)
              {    
//GWT                	
//              	owner.topToolBar.cutButton.setImage(owner.cutImage);
              
              }
//GWT                
//              owner.enableOptions(true);    
//              owner.resetHelpPoints();
              setHelpPointDrop(false);
//System.out.println("right");                                            
//numPlanes numLines

      // na stap 2 weer mouseMode = INERT
      
          }
//System.out.println("nothing");                                        
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

//GWT
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
          

//ook (tijdelijk) parallele projectie??

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
			e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY, DrawConstants.ONE_FINGER);
			
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
			
			mouseMoveTouchMoveAction(eventX, eventY, DrawConstants.ONE_FINGER, 0, 0);
			
			
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

	public int dist(int x1, int y1, int x2, int y2)
	{
		return (int) Math.round(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
	}
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler
	{
		int touchCnt = 0;
		int pinchState = DrawConstants.READY;
		int pinchStartDistance = 0;
		int pinchMoveDistance = 0;
		//int startEvent1X = 0, startEvent1Y = 0, startEvent2X = 0, startEvent2Y = 0;
		//int moveEvent1X = 0, moveEvent1Y = 0, moveEvent2X = 0, moveEvent2Y = 0;		
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				touchCnt++;
				
				if (pinchState == DrawConstants.READY)
				{
					Touch touch = e.getTouches().get(0);
				
					int eventX = touch.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int eventY = touch.getPageY() - drawingPanelCanvas.getAbsoluteTop();
					
					pinchState = DrawConstants.ONE_FINGER;
					
					mouseDownTouchStartAction(eventX, eventY, pinchState);
					
panel3D.testString2 = "down tc " + touchCnt + " ps " + pinchState;
panel3D.repaint();
					
					
				}
				else if (pinchState == DrawConstants.ONE_FINGER)
				{
					Touch touch1 = e.getTouches().get(0);
					Touch touch2 = e.getTouches().get(1);
				
					int event1X = touch1.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event1Y = touch1.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
					int event2X = touch2.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event2Y = touch2.getPageY() - drawingPanelCanvas.getAbsoluteTop();
					
					pinchState = DrawConstants.TWO_FINGERS;
					
					pinchStartDistance = dist(event1X, event1Y, event2X, event2Y);
					
					mouseDownTouchStartAction(event1X, event1Y, pinchState);
					
panel3D.testString2 = "down tc " + touchCnt + " ps " + pinchState;
panel3D.repaint();
					
				}
				
				//mouseDownTouchStartAction(startEvent1X, startEvent1Y, pinchState);
				
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
				if (pinchState == DrawConstants.ONE_FINGER)
				{	
					Touch touch = e.getTouches().get(0);

					int eventX = touch.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int eventY = touch.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
				
					mouseMoveTouchMoveAction(eventX, eventY, pinchState, 0, 0);
					
panel3D.testString2 = "move tc " + touchCnt + " ps " + pinchState;
panel3D.repaint();
					
					
				}
				else if (pinchState == DrawConstants.TWO_FINGERS)
				{
					Touch touch1 = e.getTouches().get(0);
					Touch touch2 = e.getTouches().get(1);
				
					int event1X = touch1.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event1Y = touch1.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
					int event2X = touch2.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event2Y = touch2.getPageY() - drawingPanelCanvas.getAbsoluteTop();				

					
					pinchMoveDistance = dist(event1X, event1Y, event2X, event2Y);
															
					mouseMoveTouchMoveAction(event1X, event1Y, pinchState, pinchStartDistance, pinchMoveDistance);
					
					pinchStartDistance = pinchMoveDistance;
					
panel3D.testString2 = "move tc " + touchCnt + " ps " + pinchState;
panel3D.repaint();
					
				
				}
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			touchCnt--;
			if (touchCnt <= 0)
			{	touchCnt = 0; 
				pinchState = DrawConstants.READY;
			}
			else
			{
				if (pinchState == DrawConstants.TWO_FINGERS)
				{
					pinchState = DrawConstants.ONE_FINGER;
					touchCnt = 1;
				}
				else if (pinchState == DrawConstants.ONE_FINGER)
				{
					pinchState = DrawConstants.READY;
					touchCnt = 0;
				}
				else
				{
					if (touchCnt == 2)
					{	pinchState = DrawConstants.TWO_FINGERS;
					}
				}
			}
			
			
			mouseUpTouchEndAction();
			
panel3D.testString2 = "up tc " + touchCnt + " ps " + pinchState;
panel3D.repaint();
			
		}
		
		public void onTouchCancel(TouchCancelEvent e)
		{
			touchCnt--;
			if (touchCnt <= 0)
			{	touchCnt = 0; 
				pinchState = DrawConstants.READY;
			}
			else
			{
				if (pinchState == DrawConstants.TWO_FINGERS)
				{
					pinchState = DrawConstants.ONE_FINGER;
				}
				else
				{
					if (touchCnt == 2)
						pinchState = DrawConstants.TWO_FINGERS;
				}
			}
			
			mouseUpTouchEndAction();
		}

	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY, int pinchState)
	{
		if (!draaibaar)
			return;

		if (pinchState == DrawConstants.TWO_FINGERS)
			return;
		
      	xClicked = eventX;
        yClicked = eventY;
//GWT                
//          panel3D.remove(dropMenu);                
        if ((mouseMode == INERT) || (mouseMode == FOLDOUT) || (mouseMode == CUTOBJECT))
        {   panel3D.oldX = eventX;
            panel3D.oldY = eventY;
//              startF = panel3D.clickedFacet(e.getX(), e.getY());
            xStart = eventX;
            yStart = eventY;
            dragging = true;
              
        }    

//      } // other button(s)

	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY, int pinchState, int startDistance, int newDistance)
	{
		if (pinchState == DrawConstants.TWO_FINGERS)
		{
			// grootste afmeting canvas in pixels
			double size = Math.sqrt(breedte * breedte + hoogte * hoogte);
			//zoom-eenheid per pixel
			double zoomStep = 3 * ZOOMSTEP / size;
			if (newDistance > startDistance)
			{
				int deltaDist = newDistance - startDistance;
				zoomIn(zoomStep * deltaDist);
			}
			else if (newDistance < startDistance)
			{
				int deltaDist = startDistance - newDistance;
				zoomOut(zoomStep * deltaDist);
			}
			
			
		}
		
		else if ((pinchState == DrawConstants.ONE_FINGER) && dragging)
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
                  
//hier checken voor buiten beeld                    

//System.out.println("dragging");
//dit doet WireFrame met 360 i.p.v. 180 graden
              double xTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.breedte; //getSize().width;
              double yTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.hoogte; //getSize().height;
//Peter's versie
//              double xTheta = (panel3D.oldY - e.getY()) * 5e-1d;
//              double yTheta = (panel3D.oldX - e.getX()) * 5e-1d;

             
//ECHT draaien
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
//              panel3D.rotateBy(theta, axis);
//              panel3D.rotateCake(xTheta, yTheta);
              panel3D.repaint();
              
              panel3D.oldX = eventX; //.getX();
              panel3D.oldY = eventY; //.getY();
          }
          else // not inCircle
          {
                  
//hier afkappen voor buiten beeld                    
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
                                   
//Peter's versie
//              double xTheta = (panel3D.oldY - e.getY()) * 5e-1d;
//              double yTheta = (panel3D.oldX - e.getX()) * 5e-1d;

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
      
      //else
      //{   
      //   dragging = false;
      //}    

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
//      	cutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));               
      }    
      public void mouseExited(MouseEvent e)
      {   
//GWT        	
//      	cutPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   
      }    
  } // class CutMLMML
*/    
} // class DrawingPanel2
