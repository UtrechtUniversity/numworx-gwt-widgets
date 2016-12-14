package fi.stroomdiagrammengwt.client;

import java.util.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * class for drawing the flow diagram on a Canvas using its Context 2d; <br> 
 * the layout of the flow diagram is handled by class DiagramManager;<br> 
 * this class also processes the following Mouse/TouchEvents on the Canvas:<br>
 * clicking on the traceBack and addEdge buttons of a non-root vertex<br>
 * clicking and dragging a vertex (not on the buttons)<br>
 * clicking on the addEdge button of a root<br> 
 * double or long click (not on the button) of a root to open a popup for flow input<br> 
 * double or long click on the label of any vertex (if there are labels)
 * to open a popup for label input<br>
 * double or long click on the capacityField of an edge to open a popup
 * for capacity input; <br>
 * long click on an edge to delete it (if allowed, see class Edge)<br> 
 * this class also takes care of tracing back flows and the user flow diagram
 * history; the flow diagram history is NOT saved in getState/setState;<br>   
 * tracing back flows: given a vertex, highlight all edges that contribute
 * to the flow through this vertex;<br>
 * displaying edge thickness:<br> 
 * relative: the sum of the vertical thicknesses of all edges out of a vertex
 * equals the height of the vertex (excluding label) and the individual  
 * vertical thicknesses are proportional to the capacities of the edges<br>
 * absolute: the sum of the vertical thicknesses of all edges out of a root are
 * displayed as in mode relative, for edges out of other vertices sum of the vertical
 * thicknesses is proportional to the fraction (flow in vertex divided by maximum
 * flow in the roots connected to the vertex).     
 */
public class DrawingPanel 
{	
	/**
	 * StroomDiagrammenGWT owns this class  
	 */
	static StroomDiagrammenGWT owner;
	/**
	 * constant: width of a vertex in pixels, this is also the 
	 * width of the vertex layers 
	 */
    public static int vertexWidth = 62;
    /**
     * constant: width of the trace back button on a vertex
     */
    public static int leftButtonWidth = 10;    
    /**
     * constant: width of the add adge button on a vertex 
     */
    public static int arrowButtonWidth = 15;    
    /**
     * constant: height of a vertex
     */
    public static int vertexHeight = 26;
    /**
     * constant: height of the label on a vertex 
     */
    public static int LABELHEIGHT = 20;
    /**
     * the actual label height, this is a flagg: equals 0 (no labels) of LABELHEIGHT (labels) 
     */
    public static int labelHeight = 0;
    /**
     * constant: pixel width for displaying capacities in edges
     */
    public static int edgeNumberWidth = 50;
    /**
     * constant: pixel height for displaying capacities in edges
     */
    public static int edgeNumberHeight = 28;    
    /**
     * constant: maximum distance between two vertex layers (pixels)
     */
    public static int maxLayerDistance = 110;
    /**
     * actual distance between two vertex layers
     */
    public int layerDistance = maxLayerDistance;
    /**
     * constant: offSet for work area
     */
    public static int ofSpace = 10;
    /**
     * minimum distanve between two vertices in the same layer
     */
    public static int minSpace = 5;
    /**
     * an undefined Rational (but not null)
     */
    public static Rational unDef = Rational.unDefined();

    /**
     * maximum number of vertex layers
     */
    public static int maxLayers = 16;
    /**
     * actual number of vertex layers
     */
    int numLayers = 0; 
    /**
     * constant: show flow as decimal number
     */
    public static int decMode = 0;
    /**
     * constant: show flow as percentage
     */
    public static int percMode = 1;
    /**
     * constant: show flow as a fraction
     */
    public static int fracMode = 2;
    /**
     * global flow mode
     */
    static int flowMode = decMode;
    /**
     * number of decimals for displaying capacities
     */
    public static int capDecs = 2;
    /**
     * number of decimals for displaying the flow in a vertex as decimal number
     */
    int vDecimals = 0;
    /**
     * constant: show Edge thickness (width) relative (see class Edge)
     */
    public static int relMode = 0;
    /**
     * constant: show Edge thickness (width) absolute (see class Edge)
     */
    public static int absMode = 1;
    /**
     * actual mode for showing Edge thickness (see class Edge)
     */
    int thickMode = relMode;
    /**
     * work area (Canvas as Rectangle)
     */
    Rectangle workSpace;
    /** 
     * Vector containing the roots of the flow diagram
     */
    Vector<Vertex> roots = new Vector<Vertex>();
    /**
     * Vector for finding recursively the roots connected to a Vertex
     */
    Vector<Vertex> sources = new Vector<Vertex>();
    /**
     * Vector for finding recursively the forward orbit of a Vertex
     */
    Vector<Vertex> orbit = new Vector<Vertex>();
    /**
     * Vertex whose flow is being traced back
     */
    Vertex traceFrom = null;
    /**
     * the DiagramManager for layout
     */
    static DiagramManager diagramManager;
    /**
     * all vertices created must be assigned a unique code
     * (otherwise DiagramCopies do not work correctly);
     * assign and increase this code after creating a new vertex  
     */
	public static int vertexCode = 1;
	/**
	 * the history of the flow diagrams
	 */
    static Vector<DiagramCopy> history = new Vector();
    /**
     * maximum history size
     */
    public static int MAXHISTORY = 50;

    /**
     * the Canvas for drawing on
     */
    Canvas sdGWTCanvas;
    /**
     * the Context2d of the Canvas for drawing with
     */
    Context2d sdGWTContext2d;
    
    /**
     * width of the Canvas
     */
    int breedte;
    /**
     * height of the Canvas
     */
    int hoogte;
    	
    /**
     * flagg for mouse down
     */
    boolean mouseDown = false;
    
    /**
     * isDemo == true: no changing flows and capacities, no Mouse/Touch actions
     */
    boolean isDemo = false;

    /**
     * constructor
     * @param o StroomDiagrammenGWT owning this class
     * @param b required width in pixels
     * @param h required height in pixels
     */
    public DrawingPanel(StroomDiagrammenGWT o, int b, int h)
    {   owner = o;
    	breedte = b;
    	hoogte = h;
    	// create the Canvas
    	sdGWTCanvas = Canvas.createIfSupported();
    	sdGWTCanvas.setWidth(b + "px");
    	sdGWTCanvas.setHeight(h + "px");
    	sdGWTCanvas.setCoordinateSpaceWidth(b);
    	sdGWTCanvas.setCoordinateSpaceHeight(h);
    	// set the Context2d
    	if (sdGWTCanvas != null)
    		initContext2d();
    	else
    		return;
    	// add MouseHandlers to the Canvas
		MouseHandler mouseHandler = new MouseHandler();
		sdGWTCanvas.addMouseDownHandler(mouseHandler);
		sdGWTCanvas.addMouseMoveHandler(mouseHandler);
		sdGWTCanvas.addMouseUpHandler(mouseHandler);
		// add TouchHandlers to the Canvas
		TouchHandler touchHandler = new TouchHandler();
		sdGWTCanvas.addTouchStartHandler(touchHandler);
		sdGWTCanvas.addTouchMoveHandler(touchHandler);
		sdGWTCanvas.addTouchEndHandler(touchHandler);
        // create an instance of DiagramManager
        diagramManager = new DiagramManager(this);
        defineSpaces();
    } // constructor

    /**
     * demo == true: flows, labels and capacities cannot be changed, no dragging of vertices allowed<br>
     * demo == false: flows, labels and capacities can be changed, dragging of vertices allowed	
     * @param demo true/false
     */
    public void zetIsDemo(boolean demo)
    {
      	isDemo = demo;
       	diagramManager.freezeEdges(demo);
       	diagramManager.freezeVertices(demo);
    }
 
    /**
     * get the Context2d of the DrawingPanel's Canvas
     * @return the Context2d
     */
    public Context2d getContext2d()
    {
       	return sdGWTContext2d;
    }

    /**
     * get the Canvas of the DrawingPanel
     * @return the Canvas
     */
    public Canvas getCanvas()
    {
       	return sdGWTCanvas;
    }

    /**
     * set the Context2d of the DrawingPanel's Canvas 
     */
    public void initContext2d()
    {
       	sdGWTContext2d = sdGWTCanvas.getContext2d();
    }

    /**
     * get the size of the Canvas
     * @return the Dimension of the Canvas
     */
    public Dimension getSize()
    {
     	return new Dimension(breedte, hoogte);
    }

    /**
     * add the current flow diagram to the history;
     * keep within the limit on the history size  
     */
    public static void addToHistory()
    {   
        DiagramCopy dc = diagramManager.copyDiagram();
        history.addElement(dc);
        if (history.size() > MAXHISTORY)
            history.removeElementAt(0);
        if (history.size() > 1)
        {	if (owner.terugButton != null)
        		owner.terugButton.setEnabled(true);
        }
    }

    /**
     * get the previous diagram from the history and show it
     */
    public void previousDiagram()
    {   int hisSize = history.size();
        if (hisSize > 1)
        {   history.removeElementAt(hisSize - 1);
            DiagramCopy dc = (DiagramCopy) history.lastElement();
            diagramManager.recreateDiagram(dc);
            // recreate diagram add dc to the history, but it was already there
            // as lastElement, so remove the second copy
            history.removeElementAt(hisSize - 1);
            paint();
        }
    }    
    
    /**
     * add a new root to the flow diagram and (optionally) add to history
     * @param toHistory add to history or not
     */
    public void addNewRoot(boolean toHistory)
    {   Vertex newRoot = new Vertex(true, 0);
        roots.addElement(newRoot);
        diagramManager.insertVertex(newRoot, null);
        if (toHistory)
        	addToHistory();
    }    

    /**
     * set work area and layerDistance    
     */
    public void defineSpaces()
    {   
      	workSpace = new Rectangle(0,0,breedte, hoogte);
        setLayerDistance();                          
    } // defineSpaces   

    /**
     * calculate the layerDistance, using the actual number of vertex layers   
     */
    public void setLayerDistance()
    {   if (numLayers == 0)
            layerDistance = maxLayerDistance;
        else
        {   int newLayerDistance = 
                (workSpace.width - 2 * ofSpace - 
                (numLayers + 1) * vertexWidth) /
                numLayers;
            layerDistance = Math.min(newLayerDistance, maxLayerDistance); 
        }
    }    
        
    /**
     * find out if Vertex v is close to a vertex layer and return the number
     * of that vertex layer or -1 if v is not close to any vertex layer     
     * @param v Vertex v
     * @return vertex layer number or -1
     */
    public int isInLayer(Vertex v)
    {   Rectangle vRect = new Rectangle(v.xPos, v.yPos, v.breedte, v.hoogte);
    	// v is partly out of work space
       	if (!rectangleContains(workSpace, vRect))
            return -1;
        int index = -1;    
        for (int i = 0; i < maxLayers; i++)
        {   int layerStart = workSpace.x + ofSpace + i * (layerDistance + vertexWidth);
            if (Math.abs(v.xPos - layerStart) <= vertexWidth / 5)
                index = i;
        }    
        return index;
    }
        
    /**
     * find the x-position of vertex layer lNum 
     * @param lNum vertex layer lNum
     * @return x-position
     */
    public int getLayerStart(int lNum)
    {   return workSpace.x + ofSpace +
               lNum * (layerDistance + vertexWidth);
    }    
        
    /**
     * check if Rectangle r contains Rectangle s
     * @param r Rectangle r
     * @param s Rectangle s
     * @return true/false
     */
    public boolean rectangleContains(Rectangle r, Rectangle s)
    {   return ((r.x <= s.x) && (r.y <= s.y) &&
                ((s.x + s.width) <= (r.x + r.width)) &&
                ((s.y + s.height) <= (r.y + r.height)));
    } // rectangleContains   

    /**
     * highlight (recursively) all Edges in the flow diagram that contribute to
     * the flow in Vertex v     
     * @param v Vertex v
     */
    public void traceBack(Vertex v)
    {   for (int i = 0; i < v.inEdges.size(); i++)
        {   Edge ine = (Edge) v.inEdges.elementAt(i);
            ine.highlighted = true;
            traceBack(ine.fromVertex);
        }    
        paint();
    }    
      
    /**
     * recursively find all vertices in the diagram that receive
     * flow from Vertex v (if any); the result is put in the Vector
     * orbit, which must be empty when calling this method!
     * avoid adding vertices more than once 
     * @param v Vertex whose forward orbit must be found
     */
    public void forwardOrbit(Vertex v)
    {   for (int i = 0; i < v.outEdges.size(); i++)
        {   Edge oute = (Edge) v.outEdges.elementAt(i);
            Vertex outv = oute.toVertex;
            if (!orbit.contains(outv))
                orbit.addElement(outv);
            forwardOrbit(outv);
        }    
    }    
        
    /**
     * find all roots connected to Vertex v and put the result
     * in the Vector sources; first find at least one such root,
     * then check if its forward orbit intersects the forward 
     * orbit of the roots that were not yet found  
     * @param v the Vertex of which all roots must be found 
     */
    public void traceAllSources(Vertex v)
    {   // reset
        sources.removeAllElements();
        // find some root connected to Vertex v
        traceSomeSources(v);
        // take the first
        Vertex someRoot = (Vertex) sources.elementAt(0);
        // reset orbit
        orbit.removeAllElements();
        // put forward orbit of first in 'orbit'
        forwardOrbit(someRoot);
        Vector<Vertex> someOrbit = new Vector<Vertex>();
        // copy
        for (int j = 0; j < orbit.size(); j++)
            someOrbit.addElement(orbit.elementAt(j));
        // check if forward orbit of roots not yet found 
        // intersects somOrbit, if yes, that root is 
        // connected to Vertex v
        for (int i = 0; i < roots.size(); i++)
        {   Vertex rt = (Vertex) roots.elementAt(i);
            if (!sources.contains(rt))
            {   orbit.removeAllElements();
                forwardOrbit(rt);
                boolean intersection = false;
                for (int k = 0; k < orbit.size(); k++)
                {   if (someOrbit.contains(orbit.elementAt(k)))
                        intersection = true;
                }    
                if (intersection)
                    sources.addElement(rt);
            }
        }    
    }    
        
    /**
     * recursively find at least one root of the flow diagram that is
     * connected to Vertex v, the result is put in the Vector
     * sources, which must be empty when calling this method!
     * avoid adding vertices more than once; if Vertex v is a root,
     * this finds v   
     * @param v the Vertex v of which at least one root must be found
     */
    public void traceSomeSources(Vertex v)
    {   // reset sources first elsewhere!!!!!
        if (roots.contains(v))
        {   {   if (!sources.contains(v))
                        sources.addElement(v);
            }
        }    
        else
        {   for (int i = 0; i < v.inEdges.size(); i++)
            {   Edge ine = (Edge) v.inEdges.elementAt(i);
                // check for source
                traceSomeSources(ine.fromVertex);
            }    
        }    
    }    
        
    /**
     * get the maximum of the flows in all roots connected to Vertex v
     * @param v the Vertex whose maximal root flow must be found 
     * @return the maximal flow or unDefined if there one of
     * the flows in the root is undefined 
     */
    public Rational getSourceFlow(Vertex v)
    {   traceAllSources(v);
        Rational sFlow = new Rational(0, 1, 0);
        for (int s = 0; s < sources.size(); s++)
        {   Vertex source = (Vertex) sources.elementAt(s);
            if (source.flow.isUndefined())
                sFlow.decVal = Rational.unDefined;
            else    
                sFlow.decVal = Math.max(source.flow.decVal, sFlow.decVal);
        }        
        return sFlow;
    }    

  	/**
   	 * paint the DrawingPanel
   	 */
    public void paint()
    {
       	paintComponent(sdGWTContext2d);
    }

    /**
     * paint all items in the DrawingPanel using Contect2d g
     * @param g the Context2d g
     */
    public void paintComponent(Context2d g)
    {   
     	// paint background of workspace
        g.setFillStyle(StroomDiagrammenGWT.workBackground);
        g.fillRect(workSpace.x, workSpace.y, workSpace.width, workSpace.height);
        // outline the vertex layers in light gray                       
        g.setStrokeStyle(CssColor.make(222, 222, 222));
        for (int k = 0; k < maxLayers; k++)
        {   g.moveTo(workSpace.x + ofSpace + k * (vertexWidth + layerDistance), workSpace.y);;
         	g.lineTo(workSpace.x + ofSpace + k * (vertexWidth + layerDistance),
           			 workSpace.y + workSpace.height - 1);
           	g.stroke();
           	g.beginPath();
           	g.moveTo(workSpace.x + ofSpace + vertexWidth + k * (vertexWidth + layerDistance),
                     workSpace.y);
           	g.lineTo(workSpace.x + ofSpace + vertexWidth + k * (vertexWidth + layerDistance),
           			 workSpace.y + workSpace.height - 1);
           	g.stroke();
        }    
        // outline the workspace in black
        g.setStrokeStyle(CssColor.make(0,0,0));
        g.strokeRect(workSpace.x, workSpace.y, workSpace.width, workSpace.height - 1);
        // draw the edges and vertices           
        diagramManager.drawEdges(g);
        diagramManager.drawVertices(g);
    } // paintComponent
        
    /**
     * handling Mouse Events 
     */
   	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
   	{
   		public void onMouseDown(MouseDownEvent e)
   		{
   			e.preventDefault();
   			// prevent scrolling 
   			e.stopPropagation();
   			mouseDown = true;
   			int eventX = e.getX();
   			int eventY = e.getY();
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

   	} //MLMML

   	/**
   	 * handling Touch Events 
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
   				int eventX = touch.getPageX() - sdGWTCanvas.getAbsoluteLeft();
   				int eventY = touch.getPageY() - sdGWTCanvas.getAbsoluteTop();				
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
   			    int eventX = touch.getPageX() - sdGWTCanvas.getAbsoluteLeft();
   				int eventY = touch.getPageY() - sdGWTCanvas.getAbsoluteTop();				
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
   	 * a PopupPanel for input
   	 */
   	PopupPanel aPopupPanel;
   	/**
   	 * show a PopupPanel for the input of flow in roots (appears below the root)
   	 * or the input of a label for any vertex (appears above the label)  
   	 * @param v vertex for input of flow/label
   	 * @param isLabel input for label?
   	 */
   	public void showVertexPopup(Vertex v, boolean isLabel)
   	{
   		int popupX = v.xPos + sdGWTCanvas.getAbsoluteLeft();
   		int popupY = v.yPos + v.hoogte + sdGWTCanvas.getAbsoluteTop();
   		if (isLabel)
   		{
   			popupY = v.yPos - 50 + sdGWTCanvas.getAbsoluteTop();
   		}
   		// check if another popup is open, process its input
   		if ((aPopupPanel != null) && aPopupPanel.isVisible())
   		{
   			if (aPopupPanel instanceof VertexPopup)
   			{	VertexPopup vpp = (VertexPopup) aPopupPanel;
   				if ((vpp.owner != v) && !vpp.getText().equals(""))
   					vpp.owner.processInput(vpp.getText());
   			}
   			if (aPopupPanel instanceof EdgePopup)
   			{	EdgePopup epp = (EdgePopup) aPopupPanel;
   				if (!epp.getText().equals(""))
   					epp.owner.processInput(epp.getText());
   			}
   		}
   		aPopupPanel = new VertexPopup(v.breedte, v, sdGWTContext2d, isLabel, this);
   		aPopupPanel.setPopupPosition(popupX, popupY);
   		aPopupPanel.show();
   		if (aPopupPanel instanceof VertexPopup)
   		{	VertexPopup vpp = (VertexPopup) aPopupPanel;
   			vpp.textBox.setFocus(true);
   		}	
   		paint();
   	}

   	/**
   	 * show a PopupPanel for the input of capacity in the capacity panel of an edge
   	 * @param e edge for input of capacity
   	 */
   	public void showEdgePopup(Edge e)
   	{
   		int popupX = e.capacityField.xPos + sdGWTCanvas.getAbsoluteLeft();
   		int popupY = e.capacityField.yPos + e.capacityField.hoogte + sdGWTCanvas.getAbsoluteTop();
   		// check if another popup is open, process its input
   		if ((aPopupPanel != null) && aPopupPanel.isVisible())
   		{
   			if (aPopupPanel instanceof EdgePopup)
   			{	EdgePopup epp = (EdgePopup) aPopupPanel;
   				if ((epp.owner != e) && !epp.getText().equals(""))
   					epp.owner.processInput(epp.getText());
   			}
   			if (aPopupPanel instanceof VertexPopup)
   			{	VertexPopup vpp = (VertexPopup) aPopupPanel;
   				if (!vpp.getText().equals(""))
   					vpp.owner.processInput(vpp.getText());
   			}
   		}
   		aPopupPanel = new EdgePopup(e.capacityField.breedte, e, sdGWTContext2d, this);
   		aPopupPanel.setPopupPosition(popupX, popupY);
   		aPopupPanel.show();
   		if (aPopupPanel instanceof EdgePopup)
   		{	EdgePopup epp = (EdgePopup) aPopupPanel;
   			epp.textBox.setFocus(true);
   		}	
   		paint();
   	}

   	/**
   	 * the time of the last MouseDown/TouchDown Event
   	 */
    protected long taptime;
    /**
     * List of long to save MouseDown/TouchDown Event times
     */
    protected List<Long> doubletap = new ArrayList<Long>();
        
    /**
     * was the mouse/touch held down longer then 300 milliseconds?
     * call this method at mouseUp/touchEnd
     * @return true/false
     */
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
    }
    
    /**
     * was the last MouseDown/TouchDown Event part of a double click, that is
     * two MouseDown/TouchDown Events less then 700 milliseconds apart?
     * @return true/false
     */
    protected boolean isDoubleClick() 
    {
        return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
    }

    /**
     * x position of MouseDown/TouchDown Event or MouseMove/TouchMove Event when dragging
     */
	int startx;
    /**
     * y position of MouseDown/TouchDown Event or MouseMove/TouchMove  Event when dragging
     */
	int starty;
    /**
     * change in x position of MouseMove/TouchMove Event relative to the last MouseMove/TouchMove Event 
     */
	int dx;
    /**
     * change in y position of MouseMove/TouchMove Event relative to the last MouseMove/TouchMove Event 
     */
	int dy;
	/**
	 * are we dragging?
	 */
    boolean dragging = false;
    /**
     * position of the clicked vertex at mouseDown/touchDown; necessary for reset in case dragging is illegal
     */
    Point oldPos = null;
    /**
     * vertexClicked cannot move further left then this x position
     */
    int leftBorder;
    /**
     * vertexClicked cannot move furter right then this x position 
     */
    int rightBorder;
    /**
     * the Vertex clicked (if any) 
     */
 	Vertex vertexClicked = null;
 	/**
 	 * was a label clicked?
 	 */
  	boolean labelClicked = false;
  	/**
  	 * was an addEdgeButton clicked?
  	 */
  	boolean addEdgeButtonClicked = false;
  	/**
  	 * was a trace back button clicked?
  	 */
  	boolean colorButtonClicked = false;
  	/**
  	 * the Edge clicked (if any)
  	 */
  	Edge edgeClicked = null;
  	/**
  	 * the Edge whose capacityPanel was clicked (if any)
  	 */
  	Edge edgeCapacityClicked = null;

  	/**
  	 * action after MouseDown/TouchStart Event at position (eventX,eventY)
  	 * @param eventX Event x-position
  	 * @param eventY Event y-position
  	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		// no action if flow diagram is demo
		if (isDemo)
			return;
		// remember tapTime and add to tap-list
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
        // reset
		vertexClicked = null;
		// find vertex clicked
		for (int vCnt = 0; vCnt < diagramManager.vertices.size(); vCnt++)
		{
			Vertex aVertex = (Vertex)diagramManager.vertices.elementAt(vCnt);
			if (aVertex.vertexClicked(eventX, eventY))
				vertexClicked = aVertex;
		}
		// reset
		labelClicked = false;
		addEdgeButtonClicked = false;
		colorButtonClicked = false;
		// find what part of the vertex was clicked
		if (vertexClicked != null)
		{	
			// add an Edge and finished
			if (vertexClicked.addEdgeButtonClicked(eventX, eventY))
			{
				addEdgeAction(vertexClicked);
				addEdgeButtonClicked = true;
				return;
			}
			// trace back the flow and finished
			else if (vertexClicked.colorButtonClicked(eventX, eventY))
			{
				diagramManager.lowLightEdges();
	            Vertex v = vertexClicked;
	            if (v != traceFrom)
	            {   traceFrom = v;
	                traceBack(v);
	            }
	            else
	            {   traceFrom = null;
	                paint();
	            }
	            addToHistory();
	            colorButtonClicked = true;
				return;
			}
			// ste the flagg, action at mouseUp/touchEnd
			else if (vertexClicked.labelClicked(eventX, eventY))
			{
				labelClicked = true;
				return;
			}
			// save down-position
			startx = eventX;
			starty = eventY;
			leftBorder = getLayerStart(vertexClicked.canMoveLeftTo())- 4;
			rightBorder = getLayerStart(vertexClicked.canMoveRightTo())+ 4;
			oldPos = new Point(vertexClicked.getLocation().x, vertexClicked.getLocation().y);
		}
		// reset
		edgeClicked = null;
		edgeCapacityClicked = null;
		// find Edge and EdgeCapacity clicked
		edgeClicked = diagramManager.getClickedEdge(eventX, eventY);
		edgeCapacityClicked = diagramManager.getClickedCapacity(eventX, eventY);
		// capacity has preference
		if ((edgeClicked != null) && (edgeCapacityClicked != null))
		{	edgeClicked = null;
		}
	}
  	/**
  	 * action after MouseMove/TouchMove Event at position (eventX,eventY)
  	 * @param eventX Event x-position
  	 * @param eventY Event y-position
  	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (isDemo)
			return;
		// dragging a vertex
		if ((vertexClicked != null) && !addEdgeButtonClicked && !colorButtonClicked && !labelClicked)
		{	
			dx = eventX - startx;
			dy = eventY - starty;
			// this could be a long click on a root
			if ((dx != 0) || (dy != 0))
				dragging = true;
			if (dragging == false)
				return;
			// potential new position
			int newx = vertexClicked.xPos + dx;
			int newy = vertexClicked.yPos + dy;
            if ((dx < 0) && (newx < leftBorder))
                 newx = leftBorder;
            else if ((newx > rightBorder))
                 newx = rightBorder;
            // check if v intersects edges of workSpace    
            if (!rectangleContains(workSpace, vertexClicked.getBoundingRect()))
            {   boolean deletable = (vertexClicked.outEdges.size() == 0);
                if (deletable)
                {   // delete all edges ending at vertexClicked
                    // this also deletes vertexClicked
                    for (int i = vertexClicked.inEdges.size() - 1; i >= 0; i--)
                    {   Edge ie = (Edge) vertexClicked.inEdges.elementAt(i);
                        diagramManager.deleteEdge(ie);
                    }
                    addToHistory();
                } 
                else // not deletable: put back in old position
                   	vertexClicked.setLocation(oldPos.x, oldPos.y);   
                dragging = false;
            }    
            else
               	vertexClicked.setLocation(newx, newy);   
            // only update the edges!!!!              
            diagramManager.updateEdges();
            paint();                  
            startx = eventX;
			starty = eventY;
		}	
	}	

  	/**
  	 * action after MouseUp/TouchEnd Event
  	 */
	public void mouseUpTouchEndAction()
	{	
		if (isDemo)
			return;
		// double click on vertexClicked
		if (vertexClicked != null && isDoubleClick()) 
		{
			// label: edit label
			if (labelClicked)
			{
				showVertexPopup(vertexClicked, true);
			}
			// root: edit flow
			else if (vertexClicked.root && !addEdgeButtonClicked)
			{	
				showVertexPopup(vertexClicked, false);
			}
			// clear the tap-list
            doubletap.clear();
            paint();
        }
		// long click on vertexClicked and not dragging
		else if (!dragging && vertexClicked != null && isLongClick()) 
		{
			// label: edit label
			if (labelClicked)
			{
				showVertexPopup(vertexClicked, true);
			}
			// root: edit flow
			else if (vertexClicked.root && !addEdgeButtonClicked)
			{	
				showVertexPopup(vertexClicked, false);
			}
			// clear the tap-list
			doubletap.clear();
			paint();
        }
		// double click on edgeCapacityClicked
		else if (edgeCapacityClicked != null && isDoubleClick()) 
		{
			// edit capacity
			showEdgePopup(edgeCapacityClicked);
            doubletap.clear();
            paint();
        }
		// double click on edgeCapacityClicked
		else if (edgeCapacityClicked != null && isLongClick()) 
		{
			// edit capacity
			showEdgePopup(edgeCapacityClicked);
            doubletap.clear();
            paint();
        }
		// double click on edgeClicked (not on the capacity)
		else if (edgeClicked != null && isLongClick()) 
		{
			// delete the edge if this is allowd
			boolean deleted = diagramManager.deleteEdge(edgeClicked);
			if (deleted)
				addToHistory();
            doubletap.clear();
            paint();
        } 
		// dragging a vertex 
		else if ((vertexClicked != null) && dragging)
        {   // number of vertex layer (if any) where vertex is at mouseUp/touchEnd
			int newLayerNum = isInLayer(vertexClicked);
			// a vertex layer
            if (newLayerNum >= 0)
            {   // there is no other vertex intersecting vertexClicked 
            	if (!diagramManager.intersectsVertex(vertexClicked))
                {   // add to history if vertexClicked changed vertex layer
            		boolean remember = (newLayerNum != vertexClicked.layerNum);
            		// move vertexClicked, see class DiagramManager
                    diagramManager.moveVertexTo(vertexClicked, newLayerNum);
                    if (remember)
                        addToHistory();
                }
                else // check if vertexClicked is (more or less) on top of another vertex  
                {   Vertex fv = diagramManager.fuseWith(vertexClicked);
                	// if so, fuse the two vertices
                    if (fv != null)
                    {   diagramManager.fuseVertices(vertexClicked, fv);     
                        addToHistory();
                    }
                    else // vertexclicked not enough on top of another vertex, put back on old position
                    	vertexClicked.setLocation(oldPos.x, oldPos.y);                        
               	}
             }    
             else // no vertex layer, put back in old position
            	 vertexClicked.setLocation(oldPos.x, oldPos.y);
             // find new edge positions
             diagramManager.updateEdges();    
             // do not update vertex layers!!!                
             dragging = false;	
        }    
		// remove one tap from the tap-list
		if (doubletap.size() >= 2) 
        {	doubletap.remove(0);
        }
	}	
        
	/**
	 * action after pressing the addEdgeButton of Vertex v:
	 * create a new Vertex in the vertex layer to the right of 
	 * the vertex layer of v and connect the new vertex to v with
	 * a new Edge; make sure that the sum of the capacities of
	 * all edges out of Vertex v remains 1
	 * @param v the Vertex whose addEdgeButton was pressed
	 */
    public void addEdgeAction(Vertex v) 
    {   
     	// create new vertex to be connected to v            
        Vertex newVertex = new Vertex(false, v.layerNum + 1);
        newVertex.decimals = vDecimals;
        diagramManager.insertVertex(newVertex, v);
        // initially 0
        Rational cap = new Rational(0, 1, 0);
        // the new edge is the first outedge of v: cap = 1
        if (v.outEdges.size() == 0)
          	cap = new Rational(1, 1, 1);
        // the new edge is the second outedge of v: 0.5 and 0.5     
        else if (v.outEdges.size() == 1)
        {   Edge ed = (Edge) v.outEdges.elementAt(0); 
            ed.setCapacity(new Rational(1, 2, 5e-1d), false);
            cap = new Rational(1, 2, 5e-1d);
        }
        Edge newEdge = new Edge(this, v, newVertex, cap); 
        // this sorts the outedges of v vertically
        diagramManager.addEdge(newEdge); 
        // if v is not a root, v has at least 1 incoming edge 
        if (v.layerNum > 0)
        {   // this edge exists!!
            Edge preEdge = (Edge) v.inEdges.elementAt(0);
            Vertex preVertex = preEdge.fromVertex;
            int preMode = preEdge.mode;
            // copy the values of the capacities (possible!)
            if (preVertex.outEdges.size() == v.outEdges.size())
            {   for (int i = 0; i < v.outEdges.size(); i++)
                {   Edge preOe = (Edge) preVertex.outEdges.elementAt(i);
                    Edge postOe = (Edge) v.outEdges.elementAt(i);
                    postOe.setCapacity(preOe.capacity, false);
                }    
            }    
            // copy the mode for displaying the capacities
            for (int j = 0; j < v.outEdges.size(); j++)
            {   Edge oe = (Edge) v.outEdges.elementAt(j);
                oe.setMode(preMode);
            }    
        }    
        else // v is a root, so format capacity as global
            newEdge.setMode(DrawingPanel.flowMode);                
        diagramManager.calculateDiagram();
        addToHistory();
    } // addEdgeAction   
}
