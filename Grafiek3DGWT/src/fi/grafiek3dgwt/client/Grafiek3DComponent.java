package fi.grafiek3dgwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

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

import fi.grafiek3dgwt.client.expressies.*;

/**
 * class responsible for drawing 3d-graphs, 3d-surfaces and 3d-curves;
 * the class contains a Canvas for drawing, whose Context2d is passed
 * to an instance of class Object3DContainer; the class also handles
 * mouse/touch actions on the Canvas (for rotation the 3d-object); <br>
 * the class implements the predefined 3d-object from the launch data (if any),
 * handles the examples (if any) and the modification of the 3d-object by
 * the buttons created in class Grafiek3DGWT; <br>
 * as painting is very slow, try to minimize the number of repaints
 * @author huub
 */

public class Grafiek3DComponent 
{
	/**
	 * predefined colors
	 */
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
	
    /**
     * there are no transparent colors in GWT; for transparency, paint once with global alpha
     * equal to 1, the another time with global alpha equal to a fraction, not implemeted since
     * this makes the widget very slow 
     */
    public static final CssColor transYellow = yellow; 
    public static final CssColor transCyan = cyan; 
    public static final CssColor transMagenta = magenta; 
    public static final CssColor transGreen = green; 
    
    /**
     * actual drawing colors, floorColor is a dummy 
     */
    public static CssColor axesColor = black;
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

    /**
     * constants for zooming in or -out
     */
    public static double MAXZOOM = 15e-1d;
    public static double MINZOOM = 2e-1d; 
    public static double ZOOMSTEP = 1e-1d;
    public static double defaultZoom = 7e-1d;
    /**
     * actual zoom
     */
    public double zoom = defaultZoom;
    
    /**
     * constants for projection type
     */
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    /**
     * actual projection type
     */
    public int defaultProjection = CENTRALPROJ;
    
    /**
     * constants for mouse modes
     */
    public static final int INERT = 0;
    /**
     * actual mouse mode
     */
    public int mouseMode = INERT;
    /**
     * remembering a previous mouse mode
     */
    public int oldMouseMode;
    
    /**
     * viewing angles for graphs 
     */
    double angleXG = Object3DContainer.angleXStart;
    double angleZG = Object3DContainer.angleZStart;
    /**
     * viewing angles for surfaces
     */
    double angleXS = Object3DContainer.angleXStart;
    double angleZS = Object3DContainer.angleZStart;
    /**
     * viewing angles for surfaces
     */
    double angleXC = Object3DContainer.angleXStart;
    double angleZC = Object3DContainer.angleZStart;
    
    /**
     * initial values for all axes; the actual step size 
     * on x- or y-axis is calculated as step/finerSteps;
     * used for resetting 
     */
    double xMinBegin = -2, xMaxBegin = 2, xStepBegin = 5e-1d, 
    	   yMinBegin = -2, yMaxBegin = 2, yStepBegin = 5e-1d, 
    	   zMinBegin = -2, zMaxBegin = 2, zStepBegin = 5e-1d;
    int xFinerStepsBegin = 2;
    int yFinerStepsBegin = 2;

    /**
     * actual axes values for graphs; the actual step size 
     * on x- or y-axis is calculated as step/finerSteps 
     */
    double xMinG = -2, xMaxG = 2, xStepG = 5e-1d, 
           yMinG = -2, yMaxG = 2, yStepG = 5e-1d, 
           zMinG = -2, zMaxG = 2, zStepG = 5e-1d;
	int xFinerStepsG = 2;
	int yFinerStepsG = 2;

	/**
     * actual axes values for surfaces; the actual step size 
     * on x- or y-axis is calculated as step/finerSteps;
     * the finerSteps are never changed and only used when
     * creating axes 
     */
    double xMinS = -2, xMaxS = 2, xStepS = 5e-1d, 
    	   yMinS = -2, yMaxS = 2, yStepS = 5e-1d, 
    	   zMinS = -2, zMaxS = 2, zStepS = 5e-1d;
	int xFinerStepsS = 2;
	int yFinerStepsS = 2;
    
    /**
     * actual axes values for curves; the actual step size 
     * on x- or y-axis is calculated as step/finerSteps;
     * the finerSteps are never changed and only used when
     * creating axes  
     */
    double xMinC = -2, xMaxC = 2, xStepC = 5e-1d, 
	   	   yMinC = -2, yMaxC = 2, yStepC = 5e-1d, 
	   	   zMinC = -2, zMaxC = 2, zStepC = 5e-1d;
	int xFinerStepsC = 2;
	int yFinerStepsC = 2;

	/**
	 * zoom, translate, refinement for graphs
	 */
    int zoomFactorG = 0;
    int translateXFactorG = 0;
    int translateYFactorG = 0;
    int translateZFactorG = 0;
    int finerFactorG = 0;

	/**
	 * zoom, translate for surfaces
	 */
    int zoomFactorS = 0;
    int translateXFactorS = 0;
    int translateYFactorS = 0;
    int translateZFactorS = 0;
    
	/**
	 * zoom, translate for curves
	 */
    int zoomFactorC = 0;
    int translateXFactorC = 0;
    int translateYFactorC = 0;
    int translateZFactorC = 0;
    
    
    /**
     * axes for graphs, surfaces or curves?
     */
    public boolean noAxesG = false;
    public boolean noAxesS = false;
    public boolean noAxesC = false;
    
    /**
     * floor types for axes, TRANSFLOOR is not used
     * see class Axes
     */
    public static final int NOFLOOR = 0;
    public static final int TRANSFLOOR = 1;

    /**
     * actual floor type for graphs, surfaces or curves 
     */
    int floorTypeG = NOFLOOR;
    int floorTypeS = NOFLOOR;
    int floorTypeC = NOFLOOR;

    /**
     * label types for axes, NOLABELS and ALLLABELS are not used
     */
    public static final int NOLABELS = 0;
    public static final int ENDLABELS = 1;
    public static final int ALLLABELS = 2;

    /**
     * actual axes label type for graphs, surfaces or curves
     */
    int labelTypeG = ENDLABELS;
    int labelTypeS = ENDLABELS;
    int labelTypeC = ENDLABELS;
    
    /**
     * wireframe for graphs or surfaces?
     */
    boolean wireFrameG = false;
    boolean wireFrameS = false;
    
    /**
     * central projection for graphs, surfaces or curves?
     */
    boolean centraleProjG = true;
    boolean centraleProjS = true;
    boolean centraleProjC = true;
    
    /** 
     * displaying 3d-objects
     */
    Object3DContainer panel3D;// = new Object3DContainer();

    /**
     * initial launch state
     */
    HashMap<String,Object> resetState = null;
    
    /**
     * current Object3D being showm
     */
    ObjectGroup3D currentObjectGroup;
    
    /**
     * constants for Object3D-type
     */
    public static final int FUNCTION = 0;
    public static final int SURFACE = 1;
    public static final int CURVE = 2;
    /**
     * actual Object3D type
     */
    int objectType = FUNCTION;
    
    /**
     * defaults for variable and parameter names
     */
	String varNaamX = "x";
	String varNaamY = "y";
	String paramNaam = "t";
	String paramNaamU = "u";
	String paramNaamV = "v";

	/**
	 * flagg for activating checking for asymptotes: if the defining
	 * functions for graphs, surfaces or curves have asymptotes,
	 * extra 3D-facets should be created for the Object3D near these asymptotes;
	 * the algorithm is not efficient and slows down drawing considerably,
	 * so leave this attribute as false and do not use defining functions
	 * with asymptotes 
	 */
	boolean checkForAsymptotes = false;
	
	/**
	 * the axes
	 */
	Axes axesObject;

	/**
	 * the current 3d-graph (if any)
	 */
	Expressie grafiek3DExpressie = null;
	Grafiek3D grafiek3DObject;

	/**
	 * the current 3d-surface (if any)
	 */
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

	/**
	 * the current 3d-curve (if any)
	 */
	Expressie curveXExpressie;
	Expressie curveYExpressie;
	Expressie curveZExpressie;
	double tMin = 0;
	double tMax = 2;
	int tPoints = 10;
	Curve3D curve3DObject;

	/**
	 * drawing Canvas
	 */
	Canvas grafiek3DCanvas;
	/**
	 * Context2d for drawing
	 */
	Context2d grafiek3DContext2d;
	/**
	 * width and height
	 */
	int breedte, hoogte;
	/**
	 * owner of this class
	 */
	Grafiek3DGWT owner;
	
	/**
	 * flagg for dragging (rotating the 3d-object)
	 */
	boolean dragging;

	/**
	 * constructor
	 * @param o owner
	 * @param b width
	 * @param h height
	 */
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
	  	
	  	panel3D = new Object3DContainer(grafiek3DContext2d, breedte, hoogte);
    	
    }
    
    /**
     * changing the current model to a new model
     * @param modelCode not used
     * @param reallyNew not used
     */
    public void setNewModel(int modelCode, boolean reallyNew)
    {
        setProjection(defaultProjection);
        mouseMode = INERT;
        
        currentObjectGroup = makeNewModel(modelCode);        
   	    panel3D.initializeModel(currentObjectGroup, false);

        // reset zooming HERE
   	    zoom = defaultZoom;
       	panel3D.setZoomFactor(zoom);        
        
       	panel3D.repaint();
    }    
    
    /**
     * set the viewing angles (for setState())
     */
    public void zetHoeken()
    {
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

    /**
     * set the initial viewing angles
     * @param startX initial x-angle
     * @param startZ initial z-angle
     */
    public void zetStartHoeken(double startX, double startZ)
    {

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

    /**
     * get the viewing angles (for getState())
     */
    public void getHoeken()
    {
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
    
    /**
     * create a new model with or without axes 
     * @param code not used
     * @return an ObjectGroup3D
     */
    public ObjectGroup3D makeNewModel(int code)
    {   
        ObjectGroup3D modelGroup = null;

    	axesObject = makeNewAxes();
    	
        if (((objectType == FUNCTION) && !noAxesG) ||
        	((objectType == SURFACE) && !noAxesS) ||
        	((objectType == CURVE) && !noAxesC)
           )
        {	
        	modelGroup = new ObjectGroup3D(axesObject, false);
        	modelGroup.numVertexLabels = axesObject.numVertexLabels;
        }
        
        // 3d-graph
        if (grafiek3DExpressie != null)
        {
        	grafiek3DObject = makeGrafiek3D();
        	// no axes
        	if (modelGroup == null) 
        	{	
        		modelGroup = new ObjectGroup3D(grafiek3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;
        	}
        	else // axes
        	{	
        		modelGroup.addObject3D(grafiek3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;
       		
        	}
        }

        // 3d-surface
        if (surfaceXExpressie != null)
        {	surface3DObject = makeSurface3D();
        	// axes
        	if (modelGroup == null) 
        	{	
        		modelGroup = new ObjectGroup3D(surface3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	else
        	{	
// the surface and the axes should be cut here?        		
        		modelGroup.addObject3D(surface3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	
        }

        // 3d-curve
        if (curveXExpressie != null)
        {
        	curve3DObject = makeCurve3D();
        	// no axes
        	if (modelGroup == null) 
        	{	
        		modelGroup = new ObjectGroup3D(curve3DObject, false);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        	else // axes
        	{	
// the curve and the axes should be cut here?        		
        		modelGroup.addObject3D(curve3DObject);
        		modelGroup.diameter = axesObject.getDiameter();
        		modelGroup.diamSet = true;

        	}
        }
        
        return modelGroup;
    }   

    /**
     * make a new axes Object3D
     * @return an axes Object3D, see class Axes
     */
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
    
    /**
     * create a Grafiek3D object, trim top or bottom if necessary, see class Grafiek3D; 
     * set other drawing options 
     * @return a Grafiek3D object
     */
    public Grafiek3D makeGrafiek3D()
    {	
    	grafiek3DObject = new Grafiek3D(grafiek3DExpressie, checkForAsymptotes,  
					 				    xMinG, xMaxG, xStepG, yMinG, yMaxG, yStepG, zMinG, zMaxG, zStepG, 
			 					 		varNaamX, varNaamY, xFinerStepsG, yFinerStepsG);    	 
    	objectColor = graphColor;
    	if (grafiek3DObject.trimTop)
    	{	
			grafiek3DObject = (Grafiek3D) Trim.trimObject3D(grafiek3DObject, zMaxG, Trim.ZMAX);
			
    	} // if trimTop
    	
    	if (grafiek3DObject.trimBottom)
    	{
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

    /**
     * set the Expression for a Grafiek3D object and create the model for this object
     * @param exp Expression for the 3d-graph
     */
    public void zetGrafiek3D(Expressie exp)
    {
    	grafiek3DExpressie = exp;
    	if (exp == null)
    		return;
    	
   		setNewModel(0, false);
    }

    /**
     * create a Surface3D object, trim top/bottom/front/back/sides if necessary, see class Surface3D; 
     * set other drawing options 
     * @return a Surface3D object
     */
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
			surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, zMaxS, Trim.ZMAX);    		
    	}
    	if (surface3DObject.trimBottom)
    	{	
			surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, zMinS, Trim.ZMIN);
    	}
    	if (surface3DObject.trimRight)
    	{	
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, xMaxS, Trim.XMAX);
    	}
    	if (surface3DObject.trimLeft)
    	{	
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, xMinS, Trim.XMIN);
    	}
    	if (surface3DObject.trimBack)
    	{	
    		surface3DObject = (Surface3D) Trim.trimObject3D(surface3DObject, yMaxS, Trim.YMAX);
    	}
    	if (surface3DObject.trimFront)
    	{	
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
    
    /**
     * create a Curve3D object, trim top/bottom/front/back/sides if necessary, see class Curve3D; 
     * set other drawing options 
     * @return a Curve3D object
     */
    public Curve3D makeCurve3D()
    {
    	curve3DObject = new Curve3D(curveXExpressie, curveYExpressie, curveZExpressie,
    			checkForAsymptotes, tMin, tMax, tPoints,
                xMinC, xMaxC, yMinC, yMaxC, zMinC, zMaxC,
                paramNaam);

    	objectColor = curveColor;
    
    	if (curve3DObject.trimTop)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, zMaxS, Trim.ZMAX);
    	}
    	if (curve3DObject.trimBottom)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, zMinS, Trim.ZMIN);    		
    	}
    	if (curve3DObject.trimRight)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, xMaxS, Trim.XMAX);
    	}
    	if (curve3DObject.trimLeft)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, xMinS, Trim.XMIN);
    	}
    	if (curve3DObject.trimBack)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, yMaxS, Trim.YMAX);
    	}
    	if (curve3DObject.trimFront)
    	{	
    		curve3DObject = (Curve3D) Trim.trimObject3D(curve3DObject, yMinS, Trim.YMIN);
		}	
		
	    if (centraleProjC)
	    	setProjection(CENTRALPROJ);
	    else
	    		setProjection(PARALLELPROJ);
    	
    	return curve3DObject;

    }

    /**
     * set the parameters for a Surface3D object and create a model for this object
     * @param xExp Expression in u and v for x-coordinate
     * @param yExp Expression in u and v for y-coordinate
     * @param zExp Expression in u and v for z-coordinate
     * @param uMi minimum u-value
     * @param uMa maximum u-value
     * @param uPo number of u-points
     * @param vMi minimum v-value
     * @param vMa maximum v-value
     * @param vPo number of v-points
     */
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

    /**
     * set the parameters for a Curve3D object and create a model for this object
     * @param xExp Expression in t for x-coordinate
     * @param yExp Expression in t for y-coordinate
     * @param zExp Expression in t for z-coordinate
     * @param tMi minimum t-value
     * @param tMa maximum t-value
     * @param tPo number of t-points
     */
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
    
    /**
     * set the projection
     * @param proj constant for central/parallel
     */
    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            defaultProjection = CENTRALPROJ;
        if (proj == PARALLELPROJ)    
            defaultProjection = PARALLELPROJ;    
        panel3D.setProjection(defaultProjection);
    }

    
    /**
     * reset axes-, zoom-, translate parameters, and angles 
     * @param newModel true: create a new model
     * @param objectType the objectType for the reset 
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
    
    /**
     * zoom in, note that this is done from the center of
     * the axes cube, so axis parameters are changed
     * @param newModel true: create a new model
     * @param objectType the objectType for the zoom in
     */
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

    /**
     * zoom out, note that this is done from the center of
     * the axes cube, so axis parameters are changed
     * @param newModel true: create a new model
     * @param objectType the objectType for the zoom out
     */
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

    /**
     * translate in the direction of the positive x-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the translate
     */
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

    /**
     * translate in the direction of the negative x-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the translate
     */
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
    
    /**
     * translate in the direction of the positive y-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the the translate
     */
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
    
    /**
     * translate in the direction of the negative y-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the translate
     */
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

    /**
     * translate in the direction of the positive z-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the translate
     */
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
    
    /**
     * translate in the direction of the negative z-axis 
     * @param newModel true: create a new model
     * @param objectType the objectType for the translate
     */
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

    /**
     * set the show mode for an object to its wireFrame parameter
     * @param objectType the objectType for the solid/wireframe change 
     */
    public void zetDraadFiguur(int objectType)
    {
    	if (objectType == FUNCTION)
    	{	zetDraadFiguur(wireFrameG, objectType);
    	}
    	else if (objectType == SURFACE)
    	{	zetDraadFiguur(wireFrameS, objectType);
    	}
    	
    }

    /**
     * set the show mode for an object to solid/wireframe 
     * @param b true: wireframe, false: solid
     * @param objectType the objectType for the solid/wireframe change
     */
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

    /**
     * set the fill color of an object
     * @param fc the fill color
     * @param objectType the objectType for the color change
     */
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
    		
    }

    /**
     * take more x- and y-steps to approximate a 3d-graph;
     * note that this more or less squares the number of facets
     * @param newModel true: create a new model
     * @param objectType only possible for FUNCTION 
     */
    public void zetFijner(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		xFinerStepsG += 1;
    		yFinerStepsG += 1;
    		finerFactorG++;
    		
    		if (newModel)
    		{	setNewModel(0, false);
    		}
    	}	
    }
    
    /**
     * take less x- and y-steps to approximate a 3d-graph;
     * @param newModel true: create a new model
     * @param objectType only possible for FUNCTION
     */
    public void zetGrover(boolean newModel, int objectType)
    {
    	if (objectType == FUNCTION)
    	{	
    		
    		if (finerFactorG > 0)
    		{	
    			xFinerStepsG -= 1;
    			yFinerStepsG -= 1;
    			finerFactorG--;
    		}
    		
    		if (newModel)
    		{	setNewModel(0, false);
    		}
    	}	
    }
    /**
     * omit axes 
     * @param newModel true: create a new model
     * @param objectType the objectType for omitting axes
     */
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
    
    /**
     * add axes
     * @param newModel true: create a new model
     * @param objectType the objectType for omitting axes
     */
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

    /**
     * set the type of axes labels (NONE or ENDLABELS)
     * @param newModel true: create a new model
     * @param type NONE or ENDLABELS
     * @param objectType the objectType for omitting axes
     */
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

    /**
     * set the projection type
     * @param b true: central, false: parallel
     * @param objectType the objectType changing projection type
     */
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
    }
    

    /**
     * set the options for a given GrafiekVoorbeeld
     * @param gv the GrafiekVoorbeeld
     */
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
		
	}

    /**
     * set the options for a given OppervlakVoorbeeld
     * @param ov the OppervlakVoorbeeld
     */
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
		
		
	}

    /**
     * set the options for a given KrommeVoorbeeld
     * @param kv the KrommeVoorbeeld
     */
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
		
	}

	/**
	 * inner class for handling Mouse Events
	 * @author huub
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


	/**
	 * inner class for handling Touch Event
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
	
	/**
	 * Mouse Down/Touch Start Action on the Canvas;
	 * @param eventX x-coordinate of Mouse Down/Touch Start
	 * @param eventY y-coordinate of Mouse Down/Touch Start
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
       	panel3D.oldX = eventX;
        panel3D.oldY = eventY;
        dragging = true;
              
	}
	
	/**
	 * Mouse Move/Touch Move Action on the Canvas; note that during dragging the 3d-object
	 * is NOT repainted, since painting is not fast enough 
	 * @param eventX x-coordinate of Mouse Move/Touch Move
	 * @param eventY y-coordinate of Mouse Move/Touch Move
	 */
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

            double xTheta = (panel3D.oldY - eventY) * 180.0d /
                             panel3D.breedte;
            double yTheta = (panel3D.oldX - eventX) * 180.0d /
                             panel3D.hoogte;

            panel3D.rotateCake(xTheta, yTheta);                

// do not repaint during dragg: too time-consuming
            //panel3D.repaint();
            
            panel3D.oldX = eventX;
            panel3D.oldY = eventY;

        }
        else
        {   
            dragging = false;
        }    

	}
	
	/**
	 * Mouse Up/Touch End Action on the Canvas; repaint after ending dragging the 3d-object!
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
	 * set the viewing options for the (modified) model
	 * contained in the launchdata; try to avoid creating  
	 * a new model (causing  a repaint) during this process  
	 * @param map the launch data
	 * @param repaint repaint?
	 */
	public void setState(Map<String,Object> map, boolean repaint)
	{
		
		ObjectMap b = JSONUtilities.wrapMap(map);

		// set defaults
		
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
			objectType = b.getInt("objectType");
		this.objectType = objectType;
		
		zoomStandaard(false, objectType);
		
		// FUNCTION
		if (b.containsKey("angleXG"))
			angleXG = b.getDouble("angleXG");
		if (b.containsKey("angleZG"))
			angleZG = b.getDouble("angleZG");
		this.angleXG = angleXG;
		this.angleZG = angleZG;
		
		boolean zoomGChanged = false;
		if (b.containsKey("zoomFactorG"))
			zoomFactorG = b.getInt("zoomFactorG");
		if (this.zoomFactorG != zoomFactorG)
			zoomGChanged = true;
		if (zoomFactorG < 0)
		{	for (int zUitCnt = zoomFactorG; zUitCnt < 0; zUitCnt++)
				zoomUit(false, FUNCTION);
		}
		if (zoomFactorG > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorG; zInCnt++)
				zoomIn(false, FUNCTION);
		}

		boolean transXGChanged = false;
		if (b.containsKey("translateXFactorG"))
			translateXFactorG = b.getInt("translateXFactorG");
		if (this.translateXFactorG != translateXFactorG)
			transXGChanged = true;
		if (translateXFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorG; tPlusCnt++)
				transPlusX(false, FUNCTION);
		}
		if (translateXFactorG < 0)
		{	for (int tMinCnt = translateXFactorG; tMinCnt < 0; tMinCnt++)
				transMinX(false, FUNCTION);
		}
		
		boolean transYGChanged = false;
		if (b.containsKey("translateYFactorG"))
			translateYFactorG = b.getInt("translateYFactorG");
		if (this.translateYFactorG != translateYFactorG)
			transYGChanged = true;
		if (translateYFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorG; tPlusCnt++)
				transPlusY(false, FUNCTION);
		}
		if (translateYFactorG < 0)
		{	for (int tMinCnt = translateYFactorG; tMinCnt < 0; tMinCnt++)
				transMinY(false, FUNCTION);
		}
		
		boolean transZGChanged = false;
		if (b.containsKey("translateZFactorG"))
			translateZFactorG = b.getInt("translateZFactorG");
		if (this.translateZFactorG != translateZFactorG)
			transZGChanged = true;
		if (translateZFactorG > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorG; tPlusCnt++)
				transPlusZ(false, FUNCTION);
		}
		if (translateZFactorG < 0)
		{	for (int tMinCnt = translateZFactorG; tMinCnt < 0; tMinCnt++)
				transMinZ(false, FUNCTION);
		}
		
		boolean wireFrameGChanged = false;
		if (b.containsKey("wireFrameG"))
			wireFrameG = b.getBoolean("wireFrameG");
		if (this.wireFrameG != wireFrameG)
		{	wireFrameGChanged = true;
			this.wireFrameG = wireFrameG;
		}
		
		boolean finerGChanged = false;
		if (b.containsKey("finerFactorG"))
			finerFactorG = b.getInt("finerFactorG");
		if (this.finerFactorG != finerFactorG)
			finerGChanged = true;
		if (finerFactorG > 0)
		{	for (int fPlusCnt = 0; fPlusCnt < finerFactorG; fPlusCnt++)
				zetFijner(false, FUNCTION);
		}
		if (finerFactorG < 0)
		{	for (int fMinCnt = finerFactorG; fMinCnt < 0; fMinCnt++)
				zetGrover(false, FUNCTION);
		}
		
		boolean axesGChanged = false;
		if (b.containsKey("noAxesG"))
		{	noAxesG = b.getBoolean("noAxesG");
		}
		if (b.containsKey("floorTypeG"))
			floorTypeG = b.getInt("floorTypeG");
		if (this.noAxesG != noAxesG)
		{	axesGChanged = true;
			this.noAxesG = noAxesG;
		}
		this.floorTypeG = floorTypeG;

		if (b.containsKey("labelTypeG"))
			labelTypeG = b.getInt("labelTypeG");
		this.labelTypeG = labelTypeG;
		
		boolean projGChanged = false;
		if (b.containsKey("centraleProjG"))
			centraleProjG = b.getBoolean("centraleProjG");
		if (this.centraleProjG != centraleProjG)
		{	projGChanged = true;
			this.centraleProjG = centraleProjG;
			zetCentraleProjectie(centraleProjG, FUNCTION);
		}
		
		String graphColorString = ""; 
		if (b.containsKey("graphColorString"))
		{	graphColorString = b.getString("graphColorString");
			graphColor = CssColor.make(graphColorString);
			this.graphColor = graphColor;
		}	
		
		// SURFACE
		if (b.containsKey("angleXS"))
			angleXS = b.getDouble("angleXS");
		if (b.containsKey("angleZS"))
			angleZS = b.getDouble("angleZS");
		this.angleXS = angleXS;
		this.angleZS = angleZS;
		
		boolean zoomSChanged = false;
		if (b.containsKey("zoomFactorS"))
			zoomFactorS = b.getInt("zoomFactorS");
		if (this.zoomFactorS != zoomFactorS)
			zoomSChanged = true;
		if (zoomFactorS < 0)
		{	for (int zUitCnt = zoomFactorS; zUitCnt < 0; zUitCnt++)
			zoomUit(false, SURFACE);
		}
		if (zoomFactorS > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorS; zInCnt++)
			zoomIn(false, SURFACE);
		}
		
		boolean transXSChanged = false;
		if (b.containsKey("translateXFactorS"))
			translateXFactorS = b.getInt("translateXFactorS");
		if (this.translateXFactorS != translateXFactorS)
			transXSChanged = true;
		if (translateXFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorS; tPlusCnt++)
				transPlusX(false, SURFACE);
		}
		if (translateXFactorS < 0)
		{	for (int tMinCnt = translateXFactorS; tMinCnt < 0; tMinCnt++)
				transMinX(false, SURFACE);
		}
		
		boolean transYSChanged = false;
		if (b.containsKey("translateYFactorS"))
			translateYFactorS = b.getInt("translateYFactorS");
		if (this.translateYFactorS != translateYFactorS)
			transYSChanged = true;
		if (translateYFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorS; tPlusCnt++)
				transPlusY(false, SURFACE);
		}
		if (translateYFactorS < 0)
		{	for (int tMinCnt = translateYFactorS; tMinCnt < 0; tMinCnt++)
				transMinY(false, SURFACE);
		}
		
		boolean transZSChanged = false;
		if (b.containsKey("translateZFactorS"))
			translateZFactorS = b.getInt("translateZFactorS");
		if (this.translateZFactorS != translateZFactorS)
			transZSChanged = true;
		if (translateZFactorS > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorS; tPlusCnt++)
				transPlusZ(false, SURFACE);
		}
		if (translateZFactorS < 0)
		{	for (int tMinCnt = translateZFactorS; tMinCnt < 0; tMinCnt++)
				transMinZ(false, SURFACE);
		}

		boolean wireFrameSChanged = false;
		if (b.containsKey("wireFrameS"))
		{	wireFrameS = b.getBoolean("wireFrameS");
		}
		if (this.wireFrameS != wireFrameS)
		{	wireFrameSChanged = true;
			this.wireFrameS = wireFrameS;
		}

		boolean axesSChanged = false;
		if (b.containsKey("noAxesS"))
		{	noAxesS = b.getBoolean("noAxesS");
		}
		if (b.containsKey("floorTypeS"))
			floorTypeS = b.getInt("floorTypeS");
		if (this.noAxesS != noAxesS)
		{	axesSChanged = true;
			this.noAxesS = noAxesS;
		}
		this.floorTypeS = floorTypeS;

		if (b.containsKey("labelTypeS"))
			labelTypeS = b.getInt("labelTypeS");
		this.labelTypeS = labelTypeS;

		boolean projSChanged = false;
		if (b.containsKey("centraleProjS"))
			centraleProjS = b.getBoolean("centraleProjS");
		if (this.centraleProjS != centraleProjS)
		{	projSChanged = true;
			this.centraleProjS = centraleProjS;
			zetCentraleProjectie(centraleProjS, SURFACE);
		}
		
		String surfaceColorString = "";
		if (b.containsKey("surfaceColorString"))
		{	surfaceColorString = b.getString("surfaceColorString");
			surfaceColor = CssColor.make(surfaceColorString);
			this.surfaceColor = surfaceColor;
		}	
		
		// CURVE
		if (b.containsKey("angleXC"))
			angleXC = b.getDouble("angleXC");
		if (b.containsKey("angleZC"))
			angleZC = b.getDouble("angleZC");
		this.angleXC = angleXC;
		this.angleZC = angleZC;
		
		boolean zoomCChanged = false;
		if (b.containsKey("zoomFactorC"))
			zoomFactorC = b.getInt("zoomFactorC");
		if (this.zoomFactorC != zoomFactorC)
			zoomCChanged = true;
		if (zoomFactorC < 0)
		{	for (int zUitCnt = zoomFactorC; zUitCnt < 0; zUitCnt++)
				zoomUit(false, CURVE);
		}
		if (zoomFactorC > 0)
		{	for (int zInCnt = 0; zInCnt < zoomFactorC; zInCnt++)
			zoomIn(false, CURVE);
		}

		boolean transXCChanged = false;
		if (b.containsKey("translateXFactorC"))
			translateXFactorC = b.getInt("translateXFactorC");
		if (this.translateXFactorC != translateXFactorC)
			transXSChanged = true;
		if (translateXFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateXFactorC; tPlusCnt++)
				transPlusX(false, CURVE);
		}
		if (translateXFactorC < 0)
		{	for (int tMinCnt = translateXFactorC; tMinCnt < 0; tMinCnt++)
				transMinX(false, CURVE);
		}
		
		boolean transYCChanged = false;
		if (b.containsKey("translateYFactorC"))
			translateYFactorC = b.getInt("translateYFactorC");
		if (this.translateYFactorC != translateYFactorC)
			transYSChanged = true;
		if (translateYFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateYFactorC; tPlusCnt++)
				transPlusY(false, CURVE);
		}
		if (translateYFactorC < 0)
		{	for (int tMinCnt = translateYFactorC; tMinCnt < 0; tMinCnt++)
				transMinY(false, CURVE);
		}
		
		boolean transZCChanged = false;
		if (b.containsKey("translateZFactorC"))
			translateZFactorC = b.getInt("translateZFactorC");
		if (this.translateZFactorC != translateZFactorC)
			transZSChanged = true;
		if (translateZFactorC > 0)
		{	for (int tPlusCnt = 0; tPlusCnt < translateZFactorC; tPlusCnt++)
				transPlusZ(false, CURVE);
		}
		if (translateZFactorC < 0)
		{	for (int tMinCnt = translateZFactorC; tMinCnt < 0; tMinCnt++)
				transMinZ(false, CURVE);
		}

		boolean axesCChanged = false;
		if (b.containsKey("noAxesC"))
		{	noAxesC = b.getBoolean("noAxesC");
		}
		if (b.containsKey("floorTypeC"))
			floorTypeC = b.getInt("floorTypeC");
		if (this.noAxesC != noAxesC)
		{	axesCChanged = true;	
			this.noAxesC = noAxesC;
		}
		this.floorTypeC = floorTypeC;
		
		if (b.containsKey("labelTypeC"))
			labelTypeC = b.getInt("labelTypeC");
		this.labelTypeC = labelTypeC;

		boolean projCChanged = false;
		if (b.containsKey("centraleProjC"))
			centraleProjC = b.getBoolean("centraleProjC");
		if (this.centraleProjC != centraleProjC)
		{	projCChanged = true;
			this.centraleProjC = centraleProjC;
			zetCentraleProjectie(centraleProjC, CURVE);
		}
		
		zetHoeken();
		
		// new model/repaint needed?
		if (zoomGChanged || zoomSChanged || zoomCChanged || transXGChanged || transYGChanged || transZGChanged ||
			transXSChanged || transYSChanged || transZSChanged || transXCChanged || transYCChanged || transZCChanged ||
			finerGChanged || wireFrameGChanged || wireFrameSChanged || axesGChanged || axesSChanged || axesCChanged || 
			projGChanged || projSChanged || projCChanged || repaint)
		{
			// these cause a new model to be created 
			if (noAxesG && (axesGChanged || zoomGChanged || transXGChanged || transYGChanged || transZGChanged))
			{	zetGeenAssen(true, FUNCTION);
			}
			else if (!noAxesG &&  (axesGChanged ||  zoomGChanged || transXGChanged || transYGChanged || transZGChanged))
			{	zetxyzAs(true, FUNCTION);
			}
			if (noAxesS && (axesSChanged || zoomSChanged || transXSChanged || transYSChanged || transZSChanged)) 
				zetGeenAssen(true, SURFACE);
			else if (!noAxesS && (axesSChanged || zoomSChanged || transXSChanged || transYSChanged || transZSChanged))
				zetxyzAs(true, SURFACE);
			if (noAxesC && (axesCChanged || zoomCChanged || transXCChanged || transYCChanged || transZCChanged)) 
				zetGeenAssen(true, CURVE);
			else if (!noAxesC && (axesCChanged || zoomCChanged || transXCChanged || transYCChanged || transZCChanged))
				zetxyzAs(true, CURVE);
		}

		// this causes a repaint
		zetDraadFiguur(objectType);

		// set correct button-toggle
		if ((objectType == FUNCTION) && !centraleProjG)
			owner.projectieButton.setDown(true);
		if ((objectType == FUNCTION) && centraleProjG)
			owner.projectieButton.setDown(false);
		if ((objectType == SURFACE) && !centraleProjS)
			owner.projectieButton.setDown(true);
		if ((objectType == SURFACE) && centraleProjS)
			owner.projectieButton.setDown(false);
		if ((objectType == CURVE) && !centraleProjC)
			owner.projectieButton.setDown(true);
		if ((objectType == CURVE) && centraleProjC)
			owner.projectieButton.setDown(false);

		// set correct button visibility
		if ((objectType == FUNCTION) && noAxesG)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,true);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,false);
		}
		if ((objectType == FUNCTION) && !noAxesG)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,false);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,true);
		}
		if ((objectType == SURFACE) && noAxesS)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,true);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,false);
		}
		if ((objectType == SURFACE) && !noAxesS)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,false);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,true);
		}
		if ((objectType == CURVE) && noAxesC)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,true);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,false);
		}
		if ((objectType == CURVE) && !noAxesC)
		{	if (owner.axesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.axesButton,false);
			if (owner.noAxesButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.noAxesButton,true);
		}

		if ((objectType == FUNCTION) && wireFrameG)
		{	if (owner.solidButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.solidButton,true);
			if (owner.wireButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.wireButton,false);
		}
		if ((objectType == FUNCTION) && !wireFrameG)
		{	if (owner.solidButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.solidButton,false);
			if (owner.wireButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.wireButton,true);
		}
		if ((objectType == SURFACE) && wireFrameS)
		{	if (owner.solidButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.solidButton,true);
			if (owner.wireButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.wireButton,false);
		}
		if ((objectType == SURFACE) && !wireFrameS)
		{	if (owner.solidButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.solidButton,false);
			if (owner.wireButton.getParent() == owner.rightPanel)
				owner.rightPanel.setWidgetVisible(owner.wireButton,true);
		}

	}
	

	/**
	 * get the viewing options for the (modified) model
	 * from the launch data; 
	 * @return a String,Object HashMap 
	 */
	public HashMap<String,Object> getState()
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		// state
		h.put("objectType", new Integer(objectType));
		
		// update the viewing angles of the current objectType
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
		
		h.put("graphColorString", graphColor.value());
		h.put("surfaceColorString", surfaceColor.value());
		
		return h;
	}
	
}
