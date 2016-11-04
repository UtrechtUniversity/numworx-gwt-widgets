package fi.stroomdiagrammengwt.client;

/*
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import javax.swing.JPanel;
*/
import java.util.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;

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


public class DrawingPanel 
{
	static StroomDiagrammenGWT owner;
    // size constants    
    // grid size in pixels
    public static int GRIDSIZE = 10;
    // dimensions for vertices in pixels
    // this is also layerwidth
    public static int vertexWidth = 62;
    public static int leftButtonWidth = 10;    
    public static int arrowButtonWidth = 15;    
    public static int vertexHeight = 26;
    // proposed label height
    public static int LABELHEIGHT = 20;
    // actual label height
    public static int labelHeight = 0;
    // dimensions for numbers in edges
    public static int edgeNumberWidth = 50;//38;    
    public static int edgeNumberHeight = 28;//25;    
    // maximum width between layers
    public static int maxLayerDistance = 110;
    // actual width between layers
    public int layerDistance = maxLayerDistance;
    // roundedness (capacityFields)
    public static int roundWidth = 26;
    public static int roundHeight = 14;
    // left, right, top, bottom insets workSpace (pixels)
    public static int leftSpace = 10, rightSpace = 10, 
                      topSpace = 10, bottomSpace = 10; 
    // minimum vertical distance between two vertices in the same layer
    public static int minSpace = 5;
    // value not yet defined (empty)
//    public static double unDefined = -1e9d;    
    public static Rational unDef = Rational.unDefined();
    // maximum number of layers
    public static int maxLayers = 16;
    // actual number of layers, set in defineSpaces
    int numLayers = 0; //, oldNumLayers;
    // modes for showing flows
    public static int decMode = 0;
    public static int percMode = 1;
    public static int fracMode = 2;
    // actual mode
    static int flowMode = decMode;
    // decimals for capacities
    public static int capDecs = 2;
    // decimals for vertices
    int vDecimals = 0;
    // modes for showing edge thickness
    public static int relMode = 0;
    public static int absMode = 1;
    // actual mode
    int thickMode = relMode;
    // work area
    // effective area after/before resizing
    Rectangle workSpace, oldWorkSpace;
    // first root, cannot be deleted 
//    Vertex root;
    // roots vector
    Vector roots = new Vector();
    // counting sources
    Vector sources = new Vector();
    // finding (forward) orbits
    Vector orbit = new Vector();
    
    // the vertices
    //Vector vertices = new Vector();
    
    
    // vertex being traced back
    Vertex traceFrom = null;
    
    // layout manager
    static DiagramManager diagramManager;
    
//GWT?    
    // thread for flowing
    //Thread flowThread;
    
    // flagg for showing flow
    boolean flowOn = false;
    // other attributes
    // boolean for deleteMode
    boolean deleteMode = false;
    // vertex labels?
//    boolean vertexLabels = false;
    
//GWT    
    // listener for vertex movements
    //MLMML listener;

	public static int vertexCode = 1;
    static Vector history = new Vector();
    public static int MAXHISTORY = 50;

 // for testing
    String testString = "";
    // font for testing
//GWT    
    //Font fo = new Font("Helvetica", Font.PLAIN, 11);

    //Image offScreen;
    //Graphics offGraphics;
    
    Canvas sdGWTCanvas;
    Context2d sdGWTContext2d;
    
    int breedte;
    int hoogte;
    	
//GWT?    
    //boolean realDWO = false;
    //SDInteractiePanel sdip = null;
    
    boolean mouseDown = false;
    int lastMoveX, lastMoveY;
    
    boolean isDemo = false;

    // constructor    
    public DrawingPanel(StroomDiagrammenGWT o, int b, int h)
    {   owner = o;
    
    	breedte = b;
    	hoogte = h;
        // for Container
        //setLayout(null);
     	
    	sdGWTCanvas = Canvas.createIfSupported();
    	sdGWTCanvas.setWidth(b + "px");
    	sdGWTCanvas.setHeight(h + "px");
    	sdGWTCanvas.setCoordinateSpaceWidth(b);
    	sdGWTCanvas.setCoordinateSpaceHeight(h);
    	
    	if (sdGWTCanvas != null)
    		initContext2d();
    	else
    		return;
    	
		MouseHandler mouseHandler = new MouseHandler();
		sdGWTCanvas.addMouseDownHandler(mouseHandler);
		sdGWTCanvas.addMouseMoveHandler(mouseHandler);
		sdGWTCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		sdGWTCanvas.addTouchStartHandler(touchHandler);
		sdGWTCanvas.addTouchMoveHandler(touchHandler);
		sdGWTCanvas.addTouchEndHandler(touchHandler);

        // init diagramManager
        diagramManager = new DiagramManager(this);
        
        // add listener for resizing events
        //    ComponentListener cl = new CL();
        //    addComponentListener(cl);
       
        //MouseListener ml = new ML();
        //addMouseListener(ml);
//GWT        
        //KeyListener kl = new KL();
        //    addKeyListener(kl);
        
        initialize();
    } // constructor

/*    
    public DrawingPanel(Stroomdiagrammen o, boolean realDWO, SDInteractiePanel sdip)
        {   
        	this.realDWO = realDWO;
        	this.sdip = sdip;
        	
        	owner = o;
            // for Container
            setLayout(null);    
            // init diagramManager
            diagramManager = new DiagramManager(this);
            // add listener for resizing events
            ComponentListener cl = new CL();
            addComponentListener(cl);
            MouseListener ml = new ML();
            addMouseListener(ml);
            KeyListener kl = new KL();
            addKeyListener(kl);
        } // constructor
*/        
        public void zetIsDemo(boolean demo)
        {
        	isDemo = demo;
        	diagramManager.freezeEdges(demo);
        	diagramManager.freezeVertices(demo);
        }
 
        public Context2d getContext2d()
        {
        	return sdGWTContext2d;
        }

        public Canvas getCanvas()
        {
        	return sdGWTCanvas;
        }

        public void initContext2d()
        {
        	sdGWTContext2d = sdGWTCanvas.getContext2d();
        }

        public Dimension getSize()
        {
        	return new Dimension(breedte, hoogte);
        }
        
        public static void addToHistory()
        {   //if (diagramManager.vertexLabelsChanged())
            //{   DiagramCopy dco = diagramManager.copyDiagram();
            //    history.addElement(dco);
            //}
            DiagramCopy dc = diagramManager.copyDiagram();
            history.addElement(dc);
            if (history.size() > MAXHISTORY)
                history.removeElementAt(0);
            if (history.size() > 1)
            {	//if (realDWO)
            	//	sdip.bottomPanel.previousButton.setEnabled(true);
            	//else
            		owner.terugButton.setEnabled(true);
            
            }
        }

        public void updateHistoryLabels()
        {   // go through list of all vertices present
        	Vector vertexRefs = diagramManager.getVertexRefs();
        	for (int vCnt = 0; vCnt < vertexRefs.size(); vCnt++)
        	{	Vertex v = (Vertex) vertexRefs.elementAt(vCnt);
    	        // go through list of all diagram copies
    	        for (int hCnt = 0; hCnt < history.size(); hCnt++)
        	    {   DiagramCopy dc = (DiagramCopy) history.elementAt(hCnt);
            	    // go through list of vertexcopies in each diagram copy
            	    for (int vcCnt = 0; vcCnt < dc.vertexCopies.size(); vcCnt++)
                	{   VertexCopy vc = (VertexCopy) dc.vertexCopies.elementAt(vcCnt);
                    	if (v.code == vc.code)
                    	{	
//GWT                    		
                    		//vc.labelText = v.vLabel.getText();
                    	}
                    
                	}    
            	}
            }
        }
        
        public void previousDiagram()
        {   int hisSize = history.size();
        
//System.out.println("hisSize = " + hisSize);

            if (hisSize > 1)
            {   history.removeElementAt(hisSize - 1);
                DiagramCopy dc = (DiagramCopy) history.lastElement();
//if (dc == null)
//System.out.println("dc = null");

                diagramManager.recreateDiagram(dc);
                
                paint();
            }
        }    
        
        // initialization of components etc.
        public void initialize()
        {   
        	defineSpaces();
        	
        	if (owner.breuken)
        		flowMode = fracMode;
        	
        	if (owner.absoluut)
        		thickMode = absMode;
        	
        	if (owner.labels)
        		diagramManager.setVertexLabels(true);
        	else
        		diagramManager.setVertexLabels(false);
        	
        	//dpSize = getSize();
            // set size of storageHeight
            // workSpace.height the rest 
            // takes care of borders
            //defineSpaces(false);
            if (owner.diagramCopy == null)
    		{
    //System.out.println("dc = null");        	
    	        // create and add root vertex(vertices)
            	if (owner.toonOptiesMenu)
            	{	addNewRoot(true);
            	}
            	else
            	{	for (int rCnt = 1; rCnt <= owner.numRoots; rCnt++)
            		{	addNewRoot(false);
            		}
            		addToHistory();
            		
            	}
    /*        	
        	    Vertex root = new Vertex(true, 0);
    	        roots.addElement(root);
        	    root.addEdgeButton.addMouseListener(new AddEdgeML());
    	        MLMML lis = new MLMML();
        	    root.flowField.addMouseListener(lis);
            	root.flowField.addMouseMotionListener(lis);
    	        diagramManager.insertVertex(root, null);
        	    addToHistory();
    */    	    
        	}
        	else
        	{	
        		
    //System.out.println("dc != null");    		
        		// truckje
//GWT        		
        		//owner.setSize(owner.getSize().width, owner.getSize().height + 1);
        		diagramManager.recreateDiagram(owner.diagramCopy);
        		addToHistory();
        	}
            
            
            
        }  // initialize  

        public void addNewRoot(boolean toHistory)
        {   Vertex newRoot = new Vertex(true, 0);
            roots.addElement(newRoot);
//GWT            
            //newRoot.addEdgeButton.addMouseListener(new AddEdgeML());
//GWT            
            //MLMML lis = new MLMML();
            //newRoot.flowField.addMouseListener(lis);
            //newRoot.flowField.addMouseMotionListener(lis);
            diagramManager.insertVertex(newRoot, null);
            if (toHistory)
            	addToHistory();
        }    

        // sets areas at initialize and after resizing
        public void defineSpaces()
        {   
        	workSpace = new Rectangle(0,0,breedte, hoogte);
        	oldWorkSpace = workSpace;
        				
            setLayerDistance();                          
        } // defineSpaces   
        
        // update workSpace after resizing
        public void updateWork()
        {  diagramManager.resizeDiagram(true); 
        }  // updateWork  
        
        public void setLayerDistance()
        {   if (numLayers == 0)
                layerDistance = maxLayerDistance;
            else
            {   int newLayerDistance = 
                    (workSpace.width - leftSpace - rightSpace -
                    (numLayers + 1) * vertexWidth) /
                    numLayers;
                layerDistance = Math.min(newLayerDistance, maxLayerDistance); 
            }
        }    
        
/*
        // check if r Rectangle r contains lwc    
        public boolean rectangleContains(Rectangle r, JPanel lwc) //LWContainer lwc)
        {   return ((r.x <= lwc.getLocation().x) &&
                    (r.y <= lwc.getLocation().y) &&
                    ((lwc.getLocation().x + lwc.getSize().width) <=
                     (r.x + r.width)) &&
                    ((lwc.getLocation().y + lwc.getSize().height) <=
                     (r.y + r.height)));
        }  // rectangleContains  
*/        
        public int isInLayer(Vertex v)
        {   Rectangle vRect = new Rectangle(v.xPos, v.yPos, v.breedte, v.hoogte);
        	if (!rectangleContains(workSpace, vRect))
                return -1;
            int index = -1;    
            for (int i = 0; i < maxLayers; i++)
            {   int layerStart = workSpace.x + leftSpace + i * (layerDistance + vertexWidth);
                if (Math.abs(v.xPos - layerStart) <= vertexWidth / 5)
                    index = i;
            }    
            return index;
        }    
        public int getLayerStart(int lNum)
        {   return workSpace.x + leftSpace +
                   lNum * (layerDistance + vertexWidth);
        }    
        

        // check if Rectangle r contains Rectangle s, overloaded        
        public boolean rectangleContains(Rectangle r, Rectangle s)
        {   return ((r.x <= s.x) && (r.y <= s.y) &&
                    ((s.x + s.width) <= (r.x + r.width)) &&
                    ((s.y + s.height) <= (r.y + r.height)));
        } // rectangleContains   

        // tracing flow recursively
        public void traceBack(Vertex v)
        {   for (int i = 0; i < v.inEdges.size(); i++)
            {   Edge ine = (Edge) v.inEdges.elementAt(i);
                ine.highlighted = true;
                traceBack(ine.fromVertex);
            }    
            paint();
        }    
      
        // find forward orbit of vertex v
        public void forwardOrbit(Vertex v)
        {   for (int i = 0; i < v.outEdges.size(); i++)
            {   Edge oute = (Edge) v.outEdges.elementAt(i);
                Vertex outv = oute.toVertex;
                if (!orbit.contains(outv))
                    orbit.addElement(outv);
                forwardOrbit(outv);
            }    
        }    
        
        // trace all roots connected with v
        public void traceAllSources(Vertex v)
        {   // reset
            sources.removeAllElements();
            // find some
            traceSomeSources(v);
            // take the first
            Vertex someRoot = (Vertex) sources.elementAt(0);
            // reset
            orbit.removeAllElements();
            // put forward orbit of first in 'orbit'
            forwardOrbit(someRoot);
            Vector someOrbit = new Vector();
            // copy
            for (int j = 0; j < orbit.size(); j++)
                someOrbit.addElement(orbit.elementAt(j));
            //     
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
        
        // find at least one source connected to v
        // tracing flow from sources recursively
        // avoid counting double!!
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
        
        public Rational getMaxRootFlow()
        {   Rational sFlow = new Rational(0, 1, 0);
            for (int s = 0; s < roots.size(); s++)
            {   Vertex rt = (Vertex) roots.elementAt(s);
                if (rt.flow.isUndefined())
                    return DrawingPanel.unDef;
                else    
                    sFlow.decVal = Math.max(sFlow.decVal, rt.flow.decVal);
            }    
            return sFlow;
        }    
  
//GWT
/*        
        // flow thread
        public void run()
        {   while (true)
            {   diagramManager.moveBubbles();
                try
                {   flowThread.sleep(150);
                }
                catch (InterruptedException ie) {}
            }
        }
*/        
        
        // private method to find a darker or a brighter version of color c, using the
        // HSB color model; factor determines the amount of change, a negative
        // factor produces darker colors, a positive factor brighter colors
//GWT
/*        
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
        public void update(Graphics g)
        {   paint(g);
        }

    	public void paint(Graphics g)
    	{	if (offScreen == null)
                offScreen = createImage(getSize().width, getSize().height);
            offGraphics = offScreen.getGraphics();
            offGraphics.setClip(0, 0, getSize().width, getSize().height);
            paintOpBuffer(offGraphics);
            //super.paint(og);
            g.drawImage(offScreen, 0, 0, null);
    	}
    */
        public void paint()
        {
        	paintComponent(sdGWTContext2d);
        }
        // paint
        //public void paintComponent(Graphics g)
        public void paintComponent(Context2d g)
        {   // paint backgrounds
            // workspace
            //g.setColor(Stroomdiagrammen.workBackground);
            g.setFillStyle(StroomDiagrammenGWT.workBackground);
            g.fillRect(workSpace.x, workSpace.y, workSpace.width, workSpace.height);

    /*
    // (temporary) grids (for checking)
            // work space
            int numRows = workSpace.height / GRIDSIZE;
            int numColumns = workSpace.width / GRIDSIZE;        
            // horizontal
            for (int i = 0; i < numRows + 1; i++)
            {   g.setColor(new Color(222, 222, 222));        
                g.drawLine(workSpace.x, workSpace.y + i * GRIDSIZE, 
                           workSpace.x + workSpace.width - 1, workSpace.y + i * GRIDSIZE);
            }               
            // vertical               
            for (int j = 0; j < numColumns + 1; j++)
                g.drawLine(workSpace.x + j * GRIDSIZE, workSpace.y, 
                           workSpace.x + j * GRIDSIZE, workSpace.y + workSpace.height - 1);
    */                       
                           
            // outline layers                       
            //g.setColor(new Color(222, 222, 222));
            g.setStrokeStyle(CssColor.make(222, 222, 222));
            for (int k = 0; k < maxLayers; k++)
            {   //g.drawLine(
                //   workSpace.x + leftSpace + k * (vertexWidth + layerDistance),
                //    workSpace.y, 
                //    workSpace.x + leftSpace + k * (vertexWidth + layerDistance),
                //    workSpace.y + workSpace.height - 1);
            	g.beginPath();
            	g.moveTo(workSpace.x + leftSpace + k * (vertexWidth + layerDistance), workSpace.y);;
            	g.lineTo(workSpace.x + leftSpace + k * (vertexWidth + layerDistance),
            			 workSpace.y + workSpace.height - 1);
            	g.stroke();
                //g.drawLine(
                //   workSpace.x + leftSpace + vertexWidth + k * (vertexWidth + layerDistance),
                //    workSpace.y, 
                //    workSpace.x + leftSpace + vertexWidth + k * (vertexWidth + layerDistance),
                //    workSpace.y + workSpace.height - 1);
            	g.beginPath();
            	g.moveTo(workSpace.x + leftSpace + vertexWidth + k * (vertexWidth + layerDistance),
                         workSpace.y);
            	g.lineTo(workSpace.x + leftSpace + vertexWidth + k * (vertexWidth + layerDistance),
            			 workSpace.y + workSpace.height - 1);
            	g.stroke();
                
            }    
            

            // outlines
            //g.setColor(Color.black);
            g.setStrokeStyle(CssColor.make(0,0,0));
            //g.drawRect(workSpace.x, workSpace.y, workSpace.width - 1, workSpace.height - 1);
            g.strokeRect(workSpace.x, workSpace.y, workSpace.width - 1, workSpace.height - 1);
                       
            // draw the edges and vertices           
            diagramManager.drawEdges(g);
            diagramManager.drawVertices(g);
            
    /*        
    // testing        
            g.setFont(fo);        
            int bx = workSpace.x + 2 * GRIDSIZE;
            int by = workSpace.y + GRIDSIZE;
            g.drawString(
            " " + history.size()
    // insert test string here        
    // testString
            , bx, by);
    */        
            
            
            
            // paint vertices and edge capacity fields
            //super.paint(g);               

            // fill borders to prevent drawing objects
            // outside predefined areas
            //g.setColor(Stroomdiagrammen.appletBackground);
/*            
            g.setFillStyle(StroomDiagrammenGWT.appletBackground);
            // bottom
            g.fillRect(0, workSpace.y + workSpace.height, breedte, 
                       hoogte - (workSpace.y + workSpace.height));
            // top                   
            g.fillRect(0, 0, breedte, workSpace.y);
            // left           
            g.fillRect(0, 0, workSpace.x, hoogte);
            // right                   
            g.fillRect(workSpace.x + workSpace.width, 0,
                       breedte - (workSpace.x + workSpace.width), 
                       hoogte);
*/                       
        } // paint
        
//GWT?        
        // handles for use from outside
        //public TraceML getTraceML()
        //{   return new TraceML();
        //}    
        //public AddEdgeML getAddEdgeML()
        //{   return new AddEdgeML();
        //}    
        //public MLMML getMLMML()
        //{   return new MLMML();
        //}
        
    	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
    	{
    		
    		//public void mousePressed(MouseEvent e)
    		public void onMouseDown(MouseDownEvent e)
    		{
    			
    //System.out.println("mouseDown");

    			e.preventDefault();
    			// prevent scrolling 
    			e.stopPropagation();
    			
    			mouseDown = true;
    			
    			int eventX = e.getX();
    			int eventY = e.getY();
    			
    			mouseDownTouchStartAction(eventX, eventY);
    			
    		}
    		
    		//public void mouseDragged(MouseEvent e)
    		public void onMouseMove(MouseMoveEvent e)	
    		{
    			e.preventDefault();
    			
    			// prevent scrolling
    			e.stopPropagation();
    			
    //System.out.println("mouseMov");			
    			
    			if (!mouseDown)
    				return;

    			int eventX = e.getX();
    			int eventY = e.getY();

    			mouseMoveTouchMoveAction(eventX, eventY);
    			
    			
    			
    		} // onMouseMove
    		
    		//public void mouseReleased(MouseEvent e)
    		public void onMouseUp(MouseUpEvent e)	
    		{
    			e.preventDefault();
    			// prevent scrolling
    			e.stopPropagation();

    //System.out.println("mouseUp");

    			mouseDown = false;
    		
    			mouseUpTouchEndAction(lastMoveX, lastMoveY);

    		}

    	} //MLMML


    	// tablet, dwo 
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
    //GWT check het TouchEndEvent
    			
    			mouseUpTouchEndAction(lastMoveX, lastMoveY);
    		}

    	}
    	
    	PopupPanel aPopupPanel;
    	public void showVertexPopup(Vertex v, boolean isLabel)
    	{
    		int popupX = v.xPos + sdGWTCanvas.getAbsoluteLeft();
    		int popupY = v.yPos + v.hoogte + sdGWTCanvas.getAbsoluteTop();
    		if (isLabel)
    		{
    			popupY = v.yPos - 50 + sdGWTCanvas.getAbsoluteTop();
    		}

//System.out.println("v.yPos " + v.yPos);
//System.out.println("v.hoogte " + v.hoogte);
    		// kijk of er ergens nog een popup open is
    		if ((aPopupPanel != null) && aPopupPanel.isVisible())
    		{
    			if (aPopupPanel instanceof VertexPopup)
    			{	VertexPopup vpp = (VertexPopup) aPopupPanel;
    				if (vpp.owner != v)
    					vpp.owner.processInput(vpp.getText());
    			}
    		}

    		aPopupPanel = new VertexPopup(v.breedte, v.hoogte, v, sdGWTContext2d, isLabel, this);
    		//paramEditor = schuifveld.paramEditor; 
    		//paramEditor.vulIn(parameters[epi].getParameterText());
    		aPopupPanel.setPopupPosition(popupX, popupY);
    		aPopupPanel.show();
    		if (aPopupPanel instanceof VertexPopup)
    		{	VertexPopup vpp = (VertexPopup) aPopupPanel;
    			vpp.textBox.setFocus(true);
    		}	
    		
    		paint();
    //System.out.println("ParamCC breedte = " + breedte);		
    //System.out.println("ParamCC popup breedte = " + paramEditor.breedte);		

    	}

        protected long taptime;
        protected List<Long> doubletap = new ArrayList<Long>();
        
    	int startx, starty, dx, dy;
        boolean dragging = false;
        Point oldPos = null;
        int leftBorder, rightBorder;
    	
        protected boolean isLongClick() 
        {
        	return System.currentTimeMillis() - taptime > 300;
    	}

    	protected boolean isDoubleClick() 
    	{
    	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
    	}

    	Vertex vertexClicked = null;
    	boolean labelClicked = false;
    	boolean addEdgeButtonClicked = false;
    	
		public void mouseDownTouchStartAction(int eventX, int eventY)
		{	
			if (isDemo)
				return;
			
	        taptime = System.currentTimeMillis();
	        doubletap.add(taptime);

			//Vertex vertexClicked = null;
			for (int vCnt = 0; vCnt < diagramManager.vertices.size(); vCnt++)
			{
				Vertex aVertex = (Vertex)diagramManager.vertices.elementAt(vCnt);
				if (aVertex.vertexClicked(eventX, eventY))
					vertexClicked = aVertex;
			}
			
			labelClicked = false;
			
			if (vertexClicked != null)
			{
//System.out.println("vertexClicked");	

				if (vertexClicked.addEdgeButtonClicked(eventX, eventY))
				{
//System.out.println("addEdgeButtonClicked");					
					addEdgeAction(vertexClicked);
					addEdgeButtonClicked = true;
					return;
				}
				else if (vertexClicked.colorButtonClicked(eventX, eventY))
				{
//System.out.println("colorButtonClicked");					
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
					return;
				}
				else if (vertexClicked.labelClicked(eventX, eventY))
				{
//System.out.println("labelClicked");					
					labelClicked = true;
					return;
				}
				startx = eventX;
				starty = eventY;
				//dragging = true;
				leftBorder = getLayerStart(vertexClicked.canMoveLeftTo())- 4;
				rightBorder = getLayerStart(vertexClicked.canMoveRightTo())+ 4;
				oldPos = new Point(vertexClicked.getLocation().x, vertexClicked.getLocation().y);
				
				
			}
		}
		
		public void mouseMoveTouchMoveAction(int eventX, int eventY)
		{	
			if (isDemo)
				return;
			
			if (vertexClicked != null)
			{	
				dx = eventX - startx;
				dy = eventY - starty;
				if ((dx != 0) || (dy != 0))
					dragging = true;
				
				if (dragging == false)
					return;

				int newx = vertexClicked.xPos + dx;
				int newy = vertexClicked.yPos + dy;
                if ((dx < 0) && (newx < leftBorder))
	                    newx = leftBorder;
	                else if ((newx > rightBorder))
	                    newx = rightBorder;
                // check if v intersects edges    
                if (!rectangleContains(workSpace, vertexClicked.getBoundingRect()))
                {   boolean deletable = (vertexClicked.outEdges.size() == 0);
                    if (deletable)
                    {   // put in original location
                        // and save configuration
                    	vertexClicked.setLocation(oldPos.x, oldPos.y);   
                        addToHistory();
                        for (int i = vertexClicked.inEdges.size() - 1; i >= 0; i--)
                        {   Edge ie = (Edge) vertexClicked.inEdges.elementAt(i);
                            diagramManager.deleteEdge(ie);
                        }    
                    }
                    else
                    	vertexClicked.setLocation(oldPos.x, oldPos.y);   
                    dragging = false;
                    //anker = null;
                }    
                else
                	vertexClicked.setLocation(newx, newy);   
                // only update the edges!!!!              
                diagramManager.updateEdges();
                paint();                  
	 
				startx = eventX;
				starty = eventY;
			
				lastMoveX = eventX;
				lastMoveY = eventY;
			}	
			

		}	


		public void mouseUpTouchEndAction(int lastMoveX, int lastMoveY)
		{	
			if (isDemo)
				return;
			
			if (vertexClicked != null && isDoubleClick()) 
			{
				// label en/of root?
				if (labelClicked)
				{
					showVertexPopup(vertexClicked, true);
				}
				
				else if (vertexClicked.root)
				{	
					showVertexPopup(vertexClicked, false);
				}
	            doubletap.clear();
	            
	            paint();
	        } 
			else if (!dragging && vertexClicked != null && isLongClick()) 
			{
				if (labelClicked)
				{
					showVertexPopup(vertexClicked, true);
				}
					
				else if (vertexClicked.root && !addEdgeButtonClicked)
				{	
					showVertexPopup(vertexClicked, false);
				}
		
				doubletap.clear();
				
				paint();
	        } 
			else if ((vertexClicked != null) && dragging)
	        {   int newLayerNum = isInLayer(vertexClicked);
	            if (newLayerNum >= 0)
	            {   if (!diagramManager.intersectsVertex(vertexClicked))
	                {   boolean remember = (newLayerNum != vertexClicked.layerNum);
	                    diagramManager.moveVertexTo(vertexClicked, newLayerNum);
	                    if (remember)
	                        addToHistory();
	                }
                    else  
	                {   Vertex fv = diagramManager.fuseWith(vertexClicked);
	                    if (fv != null)
	                    {   diagramManager.fuseVertices(vertexClicked, fv);     
	                        addToHistory();
                        }
	                    else
	                    	vertexClicked.setLocation(oldPos.x, oldPos.y);                        
	                }
	             }    
	             else
	            	 vertexClicked.setLocation(oldPos.x, oldPos.y);
	             diagramManager.updateEdges();    
	// do not update vertex layers!!!                
//	                diagramManager.redrawDiagram();
	                //anker = null;
	            dragging = false;	

	            if (doubletap.size() >= 2) 
	            {	//doubletap.clear();
	            	doubletap.remove(0);
	            }
	        }

		}	


        
//GWT
/*        
        class ML extends MouseAdapter
        {   public void mousePressed(MouseEvent e)
            {   requestFocus();
                Edge edge = diagramManager.getClickedEdge(e.getX(), e.getY());
                
                if ((edge != null) && 
                    (deleteMode || (e.getModifiers() & e.BUTTON3_MASK) != 0)
                   ) 
                {   diagramManager.deleteEdge(edge);
                    deleteMode = false;
                    addToHistory();
                }
            }
        }
*/
//GWT
/*        
        class KL extends KeyAdapter
        {   public void keyPressed(KeyEvent e)
            {   int kc = e.getKeyCode();
                if (kc == KeyEvent.VK_DELETE)
                {   deleteMode = true;
                }    
            }    
            public void keyReleased(KeyEvent e)
            {   int kc = e.getKeyCode();
                if (kc == KeyEvent.VK_DELETE)
                {   deleteMode = false;
                }    
            }    
        }    
*/
        
//GWT
/*        
        // inner class for vertex color button
        class TraceML extends MouseAdapter
        {   public void mousePressed(MouseEvent e)
            {   requestFocus();
                diagramManager.lowLightEdges();
                Vertex v = (Vertex) e.getComponent().getParent();
                if (v != traceFrom)
                {   traceFrom = v;
                    traceBack(v);
                }
                else
                {   traceFrom = null;
                    repaint();
                }
                addToHistory();
            }    
        }
*/
        
        // action for vertex add edge button
        public void addEdgeAction(Vertex v) 
        {   
        	// create new vertex to be connected to v            
            Vertex newVertex = new Vertex(false, v.layerNum + 1);
            newVertex.decimals = vDecimals;
            // add listeners
            //newVertex.addEdgeButton.addMouseListener(new AddEdgeML());            
            //newVertex.colorButton.addMouseListener(new TraceML());    
            //MLMML lis = new MLMML();
            //newVertex.flowField.addMouseListener(lis);
            //newVertex.flowField.addMouseMotionListener(lis);
            diagramManager.insertVertex(newVertex, v);
            // initially 0
            Rational cap = new Rational(0, 1, 0);
            // first outedge 1
            if (v.outEdges.size() == 0)
            	cap = new Rational(1, 1, 1);
            // second outedge 0.5 and 0.5     
            else if (v.outEdges.size() == 1)
            {   Edge ed = (Edge) v.outEdges.elementAt(0); 
                ed.setCapacity(new Rational(1, 2, 5e-1d), false);
                cap = new Rational(1, 2, 5e-1d);
            }
            Edge newEdge = new Edge(this, v, newVertex, cap); 
            // this sorts the outedges of v
            diagramManager.addEdge(newEdge); 
            // if v is not a root
            if (v.layerNum > 0)
            {   // this edge exists!!
                Edge preEdge = (Edge) v.inEdges.elementAt(0);
                Vertex preVertex = preEdge.fromVertex;
                int preMode = preEdge.mode;
                // copy capacities
                if (preVertex.outEdges.size() == v.outEdges.size())
                {   for (int i = 0; i < v.outEdges.size(); i++)
                    {   Edge preOe = (Edge) preVertex.outEdges.elementAt(i);
                        Edge postOe = (Edge) v.outEdges.elementAt(i);
                        postOe.setCapacity(preOe.capacity, false);
                    }    
                }    
                // copy mode
                for (int j = 0; j < v.outEdges.size(); j++)
                {   Edge oe = (Edge) v.outEdges.elementAt(j);
                    oe.setMode(preMode);
                }    
            }    
            else
                newEdge.setMode(DrawingPanel.decMode);                
                
            diagramManager.calculateDiagram();
                    
            addToHistory();
   
        } // addEdgeAction   

        
//GWT
/*        
        // listening to mouse and mouse motion events on LWContainers
        class MLMML extends MouseAdapter implements MouseMotionListener
        {   // dragging with (left) mouse button
            boolean dragging = false;
            // anker for dragging
            Point anker = null;
            // Vertex where dragg events take place
            Vertex v;
            // old position of vertex
            Point oldPos = null;
            // shift while dragging
            int dx, dy;
            int leftBorder, rightBorder;        
            
            // mouse pressed events
            public void mousePressed(MouseEvent e)
            {   //requestFocus();
                // get the vertex
                v = (Vertex) e.getComponent().getParent();
                v.flowField.requestFocus();
                // note: we are dragging the flowField of vertex v
                leftBorder = getLayerStart(v.canMoveLeftTo()) 
                             - 4;
                rightBorder = getLayerStart(v.canMoveRightTo()) 
                             + 4;
                
//                if (!deleteMode)            
//                {   
                    // waar was dit voor??
                    //owner.requestFocus();
                    dragging = true;
                    // save old position
                    oldPos = new Point(v.getLocation().x, v.getLocation().y);
                    // put on top
                    // Java 8 resistent!
                    setComponentZOrder(v,0);
                    //remove(v);
                    //add(v, 0);
                    
                    // set anker point relative to v!!!
                    anker = new Point(e.getComponent().getLocation().x + e.getX(), 
                                      e.getComponent().getLocation().y + e.getY());
//                }
//                else // deleteMode on
//                {   dragging = false;
                    //if (v != root)
                    //    diagramManager.deleteVertex(v);
                    //owner.unDelete();    
//                }    
            } // mousePressed
            
            public void mouseReleased(MouseEvent e)
            {   if ((anker != null) && dragging)
                {   int newLayerNum = isInLayer(v);
                    if (newLayerNum >= 0)
                    {   if (!diagramManager.intersectsVertex(v))
                        {   boolean remember = (newLayerNum != v.layerNum);
                            diagramManager.moveVertexTo(v, newLayerNum);
                            if (remember)
                                addToHistory();
                        }
                        else  
                        {   Vertex fv = diagramManager.fuseWith(v);
                            if (fv != null)
                            {   diagramManager.fuseVertices(v, fv);            
                                addToHistory();
                            }
                            else
                                v.setLocation(oldPos.x, oldPos.y);                        
                        }
                    }    
                    else
                        v.setLocation(oldPos.x, oldPos.y);
                    diagramManager.updateEdges();    
    // do not update vertex layers!!!                
//                    diagramManager.redrawDiagram();
                    anker = null;
                }
                dragging = false;
            } // mouseReleased
            
            // dragging 
            public void mouseDragged(MouseEvent e)
            {   // anker should be set
                if ((anker != null) && dragging)
                {   // find relative movement for v!!
                    dx = e.getComponent().getLocation().x + e.getX() - anker.x;
                    dy = e.getComponent().getLocation().y + e.getY() - anker.y;
                    // new position
                    Point newPos = new Point(
                                   v.getLocation().x + dx,
                                   v.getLocation().y + dy);
                    if ((dx < 0) && (newPos.x < leftBorder))
                        newPos.x = leftBorder;
                    else if ((newPos.x > rightBorder))
                        newPos.x = rightBorder;
                    // check if v intersects edges    
                    if (!rectangleContains(workSpace, v))
                    {   boolean deletable = (v.outEdges.size() == 0);
                        if (deletable)
                        {   // put in original location
                            // and save configuration
                            v.setLocation(oldPos.x, oldPos.y);   
                            addToHistory();
                            for (int i = v.inEdges.size() - 1; i >= 0; i--)
                            {   Edge ie = (Edge) v.inEdges.elementAt(i);
                                diagramManager.deleteEdge(ie);
                            }    
                        }
                        else
                            v.setLocation(oldPos.x, oldPos.y);   
                        dragging = false;
                        anker = null;
                    }    
                    else
                        v.setLocation(newPos.x, newPos.y);   
                    // only update the edges!!!!              
                    diagramManager.updateEdges();
                    repaint();                  
                } // if anker != null   
            } // mouseDragged       
            
            // mouse moved events, not used        
            public void mouseMoved(MouseEvent e) {}        
        } // class MLMML
*/    
}
