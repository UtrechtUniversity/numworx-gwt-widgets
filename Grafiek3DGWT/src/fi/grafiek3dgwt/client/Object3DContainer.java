package fi.grafiek3dgwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class taking care of drawing 3d-object(groups) in view space, with a choice of paint algorithms (see class Object3D),
 * paint modes (shadow, how to color the "inside" of an Object3D), projections (parallel or central); the class also handles
 * zooming in or out and mouse events on the 3d-object(groups) in view space; <br>
 * note that the drawing Canvas and its Mouse/Touch handlers are located in class Grafoek3DComponent.  
 * @author huub
 */
public class Object3DContainer
{       
	/**
	 * the 3d-objectgroup in world space
	 */
    ObjectGroup3D model;
    /**
     * Doorzien: an additional 3d-objectgroup in world space 
     */
    ObjectGroup3D previewModel;
    /** the matrix transforming world space coordinates to view space coordinates
     */
    Matrix3D mat;
    /**
     * types of painting algorithm, see class Object3D
     */
    public static final int PUREZ = 0;
    public static final int NZMINFIRST = 1;
    public static final int NONZMIN = 2;
    public static final int HYBRID1 = 3;
    public static final int HYBRID2 = 4;
    public static final int SEMIEXACT = 5;
    public static final int EXACT = 6;    

    /**
     * default painting algorithm
     */
    public int paintType = PUREZ;

    /**
     * sensitivity for clicking vertices and edges in pixels
     */
    public static final int SSTT = 4;
    
    /**
     * remembering mouse position at start of dragg
     */
    int oldX, oldY;
    /**
     * angles of the model in view space
     */
    double angleX, angleY, angleZ;
    /**
     * initial angles of the model in view space
     */
    static double angleXStart = 75;
    static double angleZStart = 25;
    /**
     * scaling factor for drawing on the canvas,
     * pixels per unit world space, incorporates zoomFactor
     */
    double scaleFac;
    /**
     * view the model from a point at distance VIEWRATIO times its size
     */
    public static int VIEWRATIO = 5;
    /**
     * flagg for shadows, if true, show facets in shades of their color
     * suggesting a light direction  
     */
    boolean shadow = true;
    /**
     * if true, show the inside of the facets of the model in gray color
     */
    boolean showInside = false;
    /**
     * background color of the drawing area 
     */
    CssColor bgColor = Grafiek3DComponent.white;
    
    /**
     * maximum distance of view point to screen i.e.
     * distance of view point to (model.origin.x, model.origin.y, 0);
     * view point is on the line through (model.origin.x, model.origin.y, 0)
     * perpendicular to the x-y-plane;
     */
    public static double MAXDISTANCE = 100000;
    /**
     * minimum distance of view point to screen 
     */
    double minDistance;
    /**
     * actual distance of view point to screen;
     * MAXDISTANCE results in parallel projection,
     * smaller gives central projection; see method paint 
     */
    double distance = MAXDISTANCE; 
    
    /**
     * constant for central projection
     */
    public static int CENTRALPROJ = 0;
    /**
     * constant for parallel projection
     */
    public static int PARALLELPROJ = 1;
    
    /**
     * actual projection value
     */
    public int projection = CENTRALPROJ;

    /**
     * zoom factor when zooming in or out
     */
    double zoomFactor = 8e-1d;
    
    /**
     * retransform data from world space tp view space?
     */
    boolean retransform = true;

    /**
     * draw a border around the drawing area?
     */
    private boolean bordered = true;
    
    /**
     * Context2d for drawing
     */
	Context2d context2d;
	
	/**
	 * width of drawing area
	 */
	int breedte;
	/**
	 * height of drawing area 
	 */
	int hoogte;

	/**
	 * testing for speed: number of paints
	 */
	int paintCnt = 0;

	/**
	 * constructor
	 * @param c2d Context2d for drawing
	 * @param b width
	 * @param h height
	 */
    public Object3DContainer(Context2d c2d, int b, int h)
    {   
    	context2d = c2d;
    	breedte = b;
    	hoogte = h;
    	mat = new Matrix3D();
    }

    /**
     * initialize the model
     * @param m the ObjectGroup3D which will become the model
     * @param newModel true causes a reset of Matrix3D and angles;
     * axes are constructed elsewhere and added to the model,
     * see class Grafiek3DComponent
     */
    public void initializeModel(ObjectGroup3D m, boolean newModel)
    {   
        model = m;
        
        if (model == null)
        {
        	return;
        }
    	
        if (newModel)
            mat = new Matrix3D();
        
        double wFac = zoomFactor * 
            ((double) breedte) / model.diameter;
        double hFac = zoomFactor * 
            ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);
        
        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // center of viewing plane corresponds to 
        // center of world model
        mat.setOrigin(((double) breedte) / 2,
                      ((double) hoogte) / 2,
                      0);

        // closer does not yet work well
        minDistance = scaleFac * model.diameter;
        if (newModel)
        {   // set initial angles
            angleX = angleXStart;
            angleY = 25;        
            angleZ = angleZStart;
            
            mat.zRotateBy(angleZ);
            mat.xRotateBy(angleX);
            
        }

    }

    /**
     * setter for background color,
     * forced to white if bordered = true
     * @param c the background color
     */
    public void setBackground(CssColor c)
    {	if (bordered) 
    		bgColor = Grafiek3DComponent.white;
    	else 
    		bgColor = c;
	}
    
    /**
     * setter for bordered; if true, background color
     * is forced to white
     * @param b true/false
     */
    public void setBordered(boolean b)
    {	bordered = b;
   		bgColor = Grafiek3DComponent.white;
    }
    
    /**
     * set the projection type: central or parallel projection
     * @param proj projection type
     */
    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            projection = CENTRALPROJ;
        if (proj == PARALLELPROJ)
            projection = PARALLELPROJ;    
    }
    /**
     * set the zoom factor and rescale Matrix3D mat
     * @param factor zoom factor
     */
    public void setZoomFactor(double factor)
    {   
    	if (model == null)
    		return;
    	
    	zoomFactor = factor;
        double wFac = zoomFactor * 
            ((double) breedte) / model.diameter;
        double hFac = zoomFactor * 
            ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);

        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // closer does not yet work well
        minDistance = scaleFac * model.diameter;
        
    }    

    /**
     * rescale Matrix3D mat
     */
    public void resetModel()
    {  
    	if (model == null) 
    		return;
        
        double wFac = zoomFactor * 
            ((double) breedte) / model.diameter;
        double hFac = zoomFactor * 
            ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);
        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // center of viewing plane corresponds to origin (0, 0, 0)
        // of world model
        mat.setOrigin(((double) breedte) / 2,
                      ((double) hoogte) / 2,
                      0);
    }

    public void repaint()
    { 	paint(context2d);
    }

    /**
     * determine viewing distance using the actual projection type, and paint
     * background, border (if any) and model
     * @param g Context2d for drawing
     */
    public void paint(Context2d g)
    {
    	paintCnt++;

		if (model == null)
		{	// outline only
	        if (bordered) 
	        {	g.setFillStyle(bgColor);
	        	g.beginPath();
	        	g.fillRect(0, 0, breedte, hoogte);
	        	g.setStrokeStyle(Grafiek3DComponent.black);
	        	g.beginPath();
	        	g.strokeRect(0, 0, breedte - 1, hoogte - 1);
	        }
	        String fontString = "16px bold sans-serif";
	        g.setFont(fontString);
	        g.setFillStyle(Grafiek3DComponent.red);
	        g.fillText("even geduld ... ", breedte / 2, hoogte / 2);
	        
			return;
        }
        // set distance in view space
        // object size is model.diameter * scaleFac
        // multiply by VIEWRATIO
        if (projection == CENTRALPROJ)
            distance = model.diameter * scaleFac * VIEWRATIO;
        if (projection == PARALLELPROJ)    
            distance = MAXDISTANCE;

        // background
        g.setFillStyle(bgColor);
        g.beginPath();
        g.fillRect(0, 0, breedte, hoogte);
        
        if (previewModel == null)
            model.paintObject3D(g, shadow, showInside, distance, mat, paintType, retransform);
                                    
        if (previewModel != null)
        {    previewModel.paintObject3D(g, shadow, showInside, distance, mat, paintType,
                                       retransform);
        }
        // outline
        g.setStrokeStyle(Grafiek3DComponent.black);
        if (bordered)
        	g.strokeRect(0, 0, breedte, hoogte);


    } // paint

    /**
     * Doorzien: setter for previewModel
     * @param pvModel the preview model
     */
    public void setPreviewModel(ObjectGroup3D pvModel)
    {   previewModel = pvModel;
    }

    /**
     * setter for showInside
     * @param b true/false
     */
    public void setShowInside(boolean b)
    {   showInside = b;
    }

    /**
     * fill the facets of the model
     * @param b true/false
     */
    public void setFilled(boolean b)
    {   model.setFilled(b);
    }    

    /**
     * set the viewing distance
     * @param d viewing distance
     */
    public void setDistance(int d)
    {   if (d > MAXDISTANCE)
            distance = MAXDISTANCE;
        else if (d <= minDistance)
            distance = minDistance;
        else
            distance = d;
    }

    /**
     * z-axis rotation
     * @param zTheta rotation degrees 
     */
    public void rotateByZ(double zTheta)
    {   
        mat.zRotateBy(zTheta);        
    }
    
    /**
     * combined x- and y-axis rotation
     * @param xTheta degrees x-axis rotation
     * @param yTheta degrees y-axis rotation
     */
    public void rotateBy(double xTheta, double yTheta)
    {   
        mat.xRotateBy(xTheta);        
        mat.yRotateBy(yTheta);
    }

    /**
     * rotation which turns Vector3D v into a multiple of Vector3D w
     * see class Matrix3D
     * @param v vector to be rotated
     * @param w target vector
     */
    public void vwRotate(Vector3D v, Vector3D w)
    {   
        mat.vwRotate(v, w);        
    }

    /**
     * rotate the model as if it was a cake on a rotating platform,
     * that is, it is not allowed to turn the object upside down;
     * that is, limit rotation around the x-axis and interpret rotation
     * around the y-axis as rotation around the z-axis
     * @param xTheta degrees x-axis rotation
     * @param yTheta degrees y-axis rotation
     */
    public void rotateCake(double xTheta, double yTheta)        
    {
         angleX += xTheta;
         angleZ += yTheta;
         if (angleX > 180)
            angleX = 180;
         if (angleX < 0)
            angleX = 0;
         mat.reset();                    
         mat.zRotateBy(angleZ);                    
         mat.xRotateBy(angleX);       
    }

    /**
     * set the x- and z-angles of the model
     * @param xAngle x-angle
     * @param zAngle z-angle
     */
    public void zetHoeken(double xAngle, double zAngle)
    {
    	angleX = 0;
    	angleZ = 0;
    	rotateCake(xAngle, zAngle);
    }
    
    /**
     * set the initial x- and z-angles of the model
     * @param xStartAngle initial x-angle
     * @param zStartAngle initial z-angle
     */
    public void zetStartHoeken(double xStartAngle, double zStartAngle)
    {
    	angleX = 0;
    	angleZ = 0;
    	angleXStart = xStartAngle;
    	angleZStart = zStartAngle;
    	rotateCake(xStartAngle, zStartAngle);
    }
    
    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
     * @return null or the Object3D in the ObjectGroup3D model which was clicked 
     */
    public Object3D objectClicked(int x, int y)
    {   return model.objectClicked(x, y, distance, mat.origin, paintType);
    }
    
    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
     * @return null or the Facet3D in model which was clicked 
     */
    public Facet3D clickedFacet(int x, int y)
    {   return model.clickedFacet(x, y, distance, mat.origin, paintType);
    }               
    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
     * @return -1 or the index of the Facet3D in model which was clicked 
     */
    public int facetClicked(int x, int y)
    {   return model.facetClicked(x, y, distance, mat.origin, paintType);
    }               

    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
	 * @return	starting point of edge clicked, end point of edge clicked and the
     * actual point on the edge which was clicked or null
     */
    public Vector3D[] edgeClicked(int x, int y)
    {   return model.edgeClicked(x, y, distance, mat.origin); 
    }                                      

    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
     * @return Facet and vertex clicked or null
     */
    public FacetWithVertex facetWithVertexClicked(int x, int y)
    {   return model.facetWithVertexClicked(x, y, distance, mat.origin);
    }                                      
    
    /**
     * shortcut, see class Object3D
     * @param x x-clicked
     * @param y y-clicked
     * @return facet and the point of the edge of this facet that was clicked or null
     */
    public FacetWithEdgePoint facetWithEdgePointClicked(int x, int y)
    {   return model.facetWithEdgePointClicked(x, y, distance, mat.origin); 
    }                                      

}


