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

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchCancelHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;

import com.google.gwt.user.client.ui.PushButton;

/**
 * class for the viewer drawing area; the class contains a Canvas for drawing and intercepting
 * Mouse and Touch Events on the Canvas; Mouse and Touch Events are limited to rotating the 3d-object<br>
 * Note: on the tablet zooming out or in is available through pinching/unpinching         
 * @author huub
 */
public class DrawingShell 
{	/**
	 * owner
	 */
    DoorzienGWT owner;

    /**
     * drawing the main 3d-object 
     */
    Object3DContainer panel3D;

    /**
     * default model code
     */
    int modelCode = DoorzienGWT.CUBE;

    /**
     * zoom constants
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
     * current projection
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
     * is the object cut into two pieces?
     */
    boolean figureCut = false;

    /**
     * mouse mode for rotating
     */
    public static final int INERT = 0;
     /**
      * mouse mode for cutting the object
      */
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
     * the initial object
     */
    Object3D originalObject;
    /**
     * the ObjectGroup3D representing the manipulated initial object  
     */
    ObjectGroup3D currentObjectGroup;
    /**
     * the two halves if the object was cut into two pieces
     */
    ObjectGroup3D cutObjectGroup;

    /**
     * plane for making a cut
     */
    Plane3D planeChoosen;

    /**
     * remembering planesFilled
     */
    boolean oldPlanesFilled;
  	/**
     * number of lines in currentObjectGroup3D
     */
    int numLines = 0;
  	/**
     * number of lines in currentObjectGroup3D
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
     * keeping track of mouseDown/TouchStart Events 
     */
    int xClicked;
    int yClicked;
    /**
     * circle radius for rotate modes
     */
    public static double RADFACTOR = 1d;
    /**
     * can the object be rotated?
     */
    boolean draaibaar = true;
    /** 
     * for testing 
     */
    String testString = "";

	/**
	 * Canvas to draw on
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
	 * flagg for dragging
	 */
	boolean dragging = false;
	
	/**
	 * circle determining the type of dragging, see method MouseMoveTouchMoveAction in class DrawingPanel2
	 */
    boolean inCircle = false;
    /**
     * start coordinates for MouseDown/TouchStart Events
     */
    int xStart, yStart;
 
    /**
     * constructor: create drawing Canvas, add handlers
     * @param o owner
     * @param b width
     * @param h height
     * @param startModel code for initial model
     */
    public DrawingShell(DoorzienGWT o, int b, int h, int startModel)
    {   owner = o;
  
  		breedte = b;
  		hoogte = h;
  
  	    drawingPanelCanvas = Canvas.createIfSupported();
  	    drawingPanelCanvas.setWidth(breedte + "px");
  	    drawingPanelCanvas.setHeight(hoogte + "px");
  	    drawingPanelCanvas.setCoordinateSpaceWidth(breedte);
  	    drawingPanelCanvas.setCoordinateSpaceHeight(hoogte);

  	    drawingPanelCanvas.addStyleName(DoorzienGWT.doorzienGWTCss.canvas());    	
  	
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
        setNewModel(startModel);
      
    }  // constructor  

    /**
     * setter for draaibaar
     * @param b true/false
     */
    public void zetDraaibaar(boolean b)
    {   draaibaar = b; 
    }
  
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
    }
  
    /**
     * changing the model to a new one, used only once at construction
     * with modelCode == CUBE;
     * drawingShell will contain the object3d from the launch data
     * or the object3d created and manipulated in the tool
     * @param modelCode model code
     */
    public void setNewModel(int modelCode)
    {   
    	numLines = 0; 
        DrawConstants.llFactor = 0;
        numPlanes = 0;
        planesFilled = false;
        figureCut = false;
        // remove slider
        setSlider(false, 0, 0, 1);
        DrawConstants.letters = false;
        setProjection(CENTRALPROJ);
        foldOutObjectGroup = null;
        cutObjectGroup = null;
        panel3D.testString = "";
        mouseMode = INERT;
		if (modelCode < DoorzienGWT.MYFIGURE)
		{
	        currentObjectGroup = makeNewModel(modelCode);        
	        setFilled(false);        
  	        panel3D.initializeModel(currentObjectGroup, true);
  	        zoom = defaultZoom;
      	    panel3D.setZoomFactor(zoom);        
        }
      
    }    

    /**
     * only used at construction with code == CUBE
     * @param code code of model, only CUBE is possible
     * @return the model as Object3D enclosed in an ObjectGroup3D
     */
    public ObjectGroup3D makeNewModel(int code)
    {   modelCode = code;
      	Object3D model;
        ObjectGroup3D modelGroup;
        model = new Box(1, 1, 1, DrawConstants.objectColor);
        model.modelCode = code;
        originalObject = model;
        modelGroup = new ObjectGroup3D(model, false);
        modelGroup.numVertexLabels = model.numVertexLabels;
        return modelGroup;
    }    

    /**
     * set the number of lines to nLines
     * @param nLines new value of numLines
     */
    public void setNumLines(int nLines)
    {   numLines = nLines;
        if (nLines == 0)
        {   DrawConstants.llFactor = 0;
        }
    }
  
    /**
     * set the number of planes to nPlanes
     * @param nPlanes the new number of planes
     */
    public void setNumPlanes(int nPlanes)
    {   numPlanes = nPlanes;
        if (nPlanes == 0)
        {    planesFilled = false;
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
        // dummy object group
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);   
        startGroup.filled = start.filled; 
        startGroup.numVertexLabels = start.numVertexLabels;
        startGroup.fixFacetArray(); //!!!
        //now build according to recipe
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
	    if (slider == null)
		    return;
    	
  	    if (b)
        {   sliderValue = init;
        	slider.setMinMax(min, max);
      	    slider.setPosition(sliderValue);
            if (mouseMode == FOLDOUT)
            {   currentFoldOut = sliderValue;
                flattened = false;
            }    
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
        {   currentFoldOut = sliderValue;
            foldOut(foldOutTreeRoot, sliderValue);
            panel3D.initializeModel(foldOutObjectGroup, false);
        }
    }

    /**
     * display all relevant objectgroups as solid objects or
     * as wireframe objects 
     * @param b true/false
     */
    public void setFilled(boolean b)
    {   filled = b;
        currentObjectGroup.setFilled(b);
        if (foldOutObjectGroup != null)
        {   foldOutObjectGroup.setFilled(b);
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
            {   
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
  
    /**
     * increase zoom by zoomStep (zoom in)
     * @param zoomStep increase in zoom
     */
    public void zoomIn(double zoomStep)
    {   double temp = zoom + zoomStep;
        if (temp <= (MAXZOOM + zoomStep / 10))
        {   zoom = temp;
            panel3D.setZoomFactor(zoom);
        }    
    }

    /**
     * decrease zoom by zoomStep (zoom out)
     * @param zoomStep decrease in zoom
     */
    public void zoomOut(double zoomStep)
    {   double temp = zoom - zoomStep;
        if (temp >= (MINZOOM - zoomStep / 10))
        {   zoom = temp;
            panel3D.setZoomFactor(zoom);
        }    
    }

    /**
     * make a fold out in two steps: <br>
     * step 0: this step is skipped, since startFacet is not null<br>   
     * step 2: make the actual fold out<br>
     * see also method makeFoldOut in class DrawigPanel2
     * Note: the fold out is made using the original object!!
     * @param stepNum number of steps (0-1)
     * @param b abort if 
     */
    public void makeFoldOut(int stepNum, boolean b)
    {   
        // button was pressed, startFacet must be choosen
        if (stepNum == 0)
        {   
        	// untoggle other relevant buttons, not used
        	if (mouseMode != INERT)
            {
                foldOutObjectGroup = null;
                cutObjectGroup = null;                
                // remove slider weg                                
                setSlider(false, 0, 0, 0);
              
                DrawConstants.TICKSVISIBLE = false;
                
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            // request for whole figure, not used
            if (!b)
            {   mouseMode = INERT;
                setNumLines(numLines);
                setNumPlanes(numPlanes);
                filled = oldFilled;
                setFilled(filled);
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
          
            foldOutObjectGroup = (ObjectGroup3D) currentObjectGroup.deepCopy();
            foldOutObject = foldOutObjectGroup.leftMostLeaf(); 
          
            foldOutObject.loosenVertices();

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
            	makeFoldOut(1, true);
            }
            else // not used
            {	
            }
          
        }     
        else if (stepNum == 1)
        {   
            foldOutObjectGroup.setTickMarks(0);

			// asumed >= 0    
			int startIndex = NoSer.containsFacet(foldOutObject, startFacet);
          
          
            // init facet labels as false
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
      
            // set replaced facets invisible
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

            oldFilled = filled;
            oldPos = Matrix3D.copy(panel3D.mat);
            setFilled(true);
            panel3D.initializeModel(foldOutObjectGroup, false);

            setSlider(true, foldOutInit, 0, 1);
        } // else if (stepNum == 1)
    }
  
  
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


    /**
     * version == EPN: modified fold out for a cylinder starting at a side(!) facet: 
     * add top and bottom of the cylinder to the side facet, then proceed as usual 
     * @param startNode FoldOutTreeNode containing the side facet
     */
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
            //always add startFacet since this contains
            //the axisdata; must also be tehre is tehre are no replacements
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
     * cut the object in two pieces along a plane;
     * step 0: this step is skipped since the plane by which to cut the object is known;<br>  
     * step 1: cut the object into two pieces
     * @param stepNum the step number (0 or 1)
     * @param b here always true
     */
    public void cutObject(int stepNum, boolean b)
    { 
        if (stepNum == 0)
        {
            if (mouseMode != INERT)
            {             
                foldOutObjectGroup = null;
                cutObjectGroup = null;
                // remove slider                                
                setSlider(false, 0, 0, 0);
                originalObject = currentObjectGroup.leftMostLeaf();
                DrawConstants.TICKSVISIBLE = false;                    
                panel3D.initializeModel(currentObjectGroup, false);                    
            }
            figureCut = b;            
            // not used here
            if (!figureCut)
            {   mouseMode = INERT;
                setNumLines(numLines);
                setNumPlanes(numPlanes);
                planesFilled = oldPlanesFilled;
  	            fillPlanes(planesFilled);
                panel3D.testString = "";
                return;   
            }    
            mouseMode = CUTOBJECT;
            Vector construction = new Vector();
            if (currentObjectGroup instanceof ObjectWithLine)
                construction = ((ObjectWithLine) currentObjectGroup).getConstruction();
            else if (currentObjectGroup instanceof ObjectWithPlane)
                construction = ((ObjectWithPlane) currentObjectGroup).getConstruction();
            if (numPlanes > 1)
            {
                planeChoosen = null;
                // wait for mouse action
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
    }
  
    /**
     * make a maximal fold out and display
     * this as a flat object  
     */
    public void flattenAction()
    {
        // roteer de foldOutGroup in view space
        Vector3D from = new Vector3D(
            startFacet.unitNormal.x,
            startFacet.unitNormal.y,
            startFacet.unitNormal.z);
        Vector3D to = new Vector3D(0, 0, 1);
        panel3D.vwRotate(from, to);
        // put slider to 100% (makes maximal fold out)
        processSlider(1);
        slider.setPosition(1);
    }

	/**
	 * inner class for handling Mouse Events; note that the class also processes (un)pinching: <br>
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

	/**
	 * integer distance between to points with integer coordinates
	 * @param x1 x-coordinate first point
	 * @param y1 y-coordinate first point
	 * @param x2 x-coordinate second point
	 * @param y2 y-coordinate second point
	 * @return integer distance
	 */
	public int dist(int x1, int y1, int x2, int y2)
	{
		return (int) Math.round(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
	}
	
	/**
	 * inner class for handling Touch Events; note that the class also processes (un)pinching: <br>
	 * @author huub
	 */
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler
	{
		/**
		 * number of fingers touching the tablet
		 */
		int touchCnt = 0;
		/**
		 * the pinch state: READY, ONE_FINGER or TWO_FINGERS
		 */
		int pinchState = DrawConstants.READY;
		/**
		 * distance between the first and second finger touching the tablet 
		 */
		int pinchStartDistance = 0;
		/**
		 * new distance between the first and second finger touching the tablet after moving one of the fingeres
		 */
		int pinchMoveDistance = 0;
		
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
					
				}
				// this Touch is a second finger touching the tablet surface
				else if (pinchState == DrawConstants.ONE_FINGER)
				{
					// first finger location
					Touch touch1 = e.getTouches().get(0);
					// second finger location
					Touch touch2 = e.getTouches().get(1);
				
					int event1X = touch1.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event1Y = touch1.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
					int event2X = touch2.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int event2Y = touch2.getPageY() - drawingPanelCanvas.getAbsoluteTop();
					
					pinchState = DrawConstants.TWO_FINGERS;
					
					pinchStartDistance = dist(event1X, event1Y, event2X, event2Y);
					
					mouseDownTouchStartAction(event1X, event1Y, pinchState);
					
				}
				
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
				// one finger down: rotating
				if (pinchState == DrawConstants.ONE_FINGER)
				{	
					Touch touch = e.getTouches().get(0);

					int eventX = touch.getPageX() - drawingPanelCanvas.getAbsoluteLeft();
					int eventY = touch.getPageY() - drawingPanelCanvas.getAbsoluteTop();				
				
					mouseMoveTouchMoveAction(eventX, eventY, pinchState, 0, 0);
					
				}
				// distance between the two fingers changed: zooming 
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
					
				}
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			touchCnt--;
			// correct touchCnt, can become negative, unknown why
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

	/**
	 * action at MouseDown/TouchStart Event:
	 * prepare rotating 
	 * @param eventX x-coordinate of Event
	 * @param eventY y-coordinate of Event
	 * @param pinchState current pinch State
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY, int pinchState)
	{
		if (!draaibaar)
			return;

		if (pinchState == DrawConstants.TWO_FINGERS)
			return;
		
      	xClicked = eventX;
        yClicked = eventY;
        // prepare rotating
        if ((mouseMode == INERT) || (mouseMode == FOLDOUT) || (mouseMode == CUTOBJECT))
        {   panel3D.oldX = eventX;
            panel3D.oldY = eventY;
            xStart = eventX;
            yStart = eventY;
            dragging = true;
        }    

	}
	
	/**
	 * action action at MouseMove/TouchMove Event
	 * two types of action are possible:<br>
	 * pinching results in zooming in or out<br>
	 * dragging results in rotating, see class DrawingPanel2
	 * @param eventX x-coordinate of Event
	 * @param eventY y-coordinate of Event
	 * @param pinchState value of current pinch state
	 * @param startDistance distance between two fingers on the tablet before the TouchMove Event
	 * @param newDistance distance between two fingers on the tablet after the TouchMove Event 
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY, int pinchState, int startDistance, int newDistance)
	{
		// pinching
		if (pinchState == DrawConstants.TWO_FINGERS)
		{
			// maximum of width/height of Canvas 
			double size = Math.sqrt(breedte * breedte + hoogte * hoogte);
			// calculate amount of zoom per pixel
			double zoomStep = 6 * ZOOMSTEP / size;
			
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
		// dragging
		else if ((pinchState == DrawConstants.ONE_FINGER) && dragging)
        {

            int xCenter = panel3D.breedte / 2; 
            int yCenter = panel3D.hoogte / 2; 
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

                 if (eventX < centerX)
                     yTheta = (panel3D.oldY - eventY) * 180.0d / panel3D.hoogte; //getSize().height;
                 else                    
                     yTheta = (eventY - panel3D.oldY) * 180.0d / panel3D.hoogte; //getSize().height;
                 if (eventY < centerY)             
                     xTheta = (eventX - panel3D.oldX) * 180.0d / panel3D.breedte; //getSize().width;
                 else                 
                     xTheta = (panel3D.oldX - eventX) * 180.0d / panel3D.breedte;//getSize().width;
                                   
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

  
} // class DrawingShell
