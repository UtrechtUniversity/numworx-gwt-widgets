package fi.grafiek3dgwt.client;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

// class representing the drawing area (a Canvas), also initializes and
// manipulates the 3D model
public class Object3DContainer// extends JPanel
{       
    // the 3D object(s) in worldspace
    ObjectGroup3D model, previewModel;
    // transforming world space to view space
    Matrix3D mat;
    // types of Painter's algo
    public static final int PUREZ = 0;
    public static final int NZMINFIRST = 1;
    public static final int NONZMIN = 2;
    public static final int HYBRID1 = 3;
    public static final int HYBRID2 = 4;
    public static final int SEMIEXACT = 5;
    public static final int EXACT = 6;    
    
    // default
    public int paintType = PUREZ;

    // sensitivity for clicking vertices and edges in pixels
    public static final int SSTT = 4;
    
    // for remembering mouse position at start of dragg
    int oldX, oldY;
    // remembering angles
    double angleX, angleY, angleZ;
    static double angleXStart = 75;
    static double angleZStart = 25;
    // scaling factor for drawing on the canvas
    // pixels per unit world space, incorporates zoomFactor
    double scaleFac;
    // view object from a point at distance VIEWRATIO times its size
    public static int VIEWRATIO = 5;
    // schadows
    boolean shadow = true;
    // showing inside in gray
    //boolean showInside = true;
    boolean showInside = false;
    // background color
    CssColor bgColor = Grafiek3DComponent.white;
    
    // distance of view point to screen i.e.
    // distance to z-plane through (model.origin.x, model.origin.y, 0)
    public static double MAXDISTANCE = 100000;
    double minDistance;
    double distance = MAXDISTANCE; // set in paint for perspective
    
    // projections
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    
    public int projection = CENTRALPROJ;
    
    double zoomFactor = 8e-1d;//9e-1d;
    boolean retransform = true;
    
    Vector3D helpStart;
    Point pt1 = new Point();
    Point pt2 = new Point();
    CssColor helpLineColor = null;
    boolean helpLine = false;

    Point hp = new Point();
    CssColor helpPointColor = null;
    boolean helpPoint = false;
    // point size
    public static int POINT = 14;
    
    // dash length in pixels
    public static int DASH = 4;

//GWT?    
    //public Font vertexFont = new Font("SansSerif", Font.ITALIC, 12);        
    //public Font textFont = new Font("SansSerif", Font.PLAIN, 12);    
    
    private boolean bordered = true;
    
	Context2d context2d;
	int breedte, hoogte;

	int paintCnt = 0;

// testing    
public static String testString = "";    
    
    // constructor
    public Object3DContainer(Context2d c2d, int b, int h)
    {   
    	context2d = c2d;
    	breedte = b;
    	hoogte = h;
    	
    	mat = new Matrix3D();
    }

    // assen extern maken en toevoegen    
    public void initializeModel(ObjectGroup3D m, boolean newModel)
    {   
        model = m;
        
        if (model == null)
        {
//        	repaint();
        	return;
        }
    	
        if (newModel)
            mat = new Matrix3D();
        
        double wFac = zoomFactor * 
            ((double) breedte) / model.diameter;
        double hFac = zoomFactor * 
            ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);
        
//System.out.println("sf = " + scaleFac);
//System.out.println("diam = " + model.diameter);

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
        // set initial angles
        
        if (newModel)
        {
            angleX = angleXStart;
            angleY = 25;        
            angleZ = angleZStart;
            
            mat.zRotateBy(angleZ);
            mat.xRotateBy(angleX);
            
        }

        //repaint();
    }
    
    public void setBackground(CssColor c)
    {	if (bordered) 
    		bgColor = Grafiek3DComponent.white;
    	else 
    		bgColor = c;
	}
    
    public void setBordered(boolean b)
    {	bordered = b;
    	//if (!b) 
    	//	bgColor = getBackground();
    	//else 
    		bgColor = Grafiek3DComponent.white;
    }
    
    

    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            projection = CENTRALPROJ;
        if (proj == PARALLELPROJ)
            projection = PARALLELPROJ;    
        //repaint();

    }
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
        
        //repaint();
    }    

// niet nodig gebruik initialize met false??
// zorg dat nieuwe afmetingen extern berekend worden??
    // iets met reset model wanneer de afmeting van de container
    // of de objectGroup3D model extern veranderd is
// eventueel met boolean resize    

    public void resetModel()
    {   //model.findCenter();
        //model.findDiameter();
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
//        repaint();
    }

    public void repaint()
    { 	paint(context2d);
    }
    
    public void paint(Context2d g)
    {
    	paintCnt++;
    	
		//Graphics2D g = (Graphics2D) gr;
		
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
    	
System.out.println("paint " + paintCnt);    	

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
	        
//System.out.println("geduld");	        
	        
			return;
        }
        // set distance in view space
        // object size is model.diameter * scaleFac
        // multiply by VIEWRATIO
        if (projection == CENTRALPROJ)
            distance = model.diameter * scaleFac * VIEWRATIO;// zoomFactor;
        if (projection == PARALLELPROJ)    
            distance = MAXDISTANCE;
//System.out.println("dis = " + distance);        

        // background
        
        //g.setGlobalAlpha(1.0d);
        
        g.setFillStyle(bgColor);
        g.beginPath();
        g.fillRect(0, 0, breedte, hoogte);
        
        //g.setGlobalAlpha(0.8d);        
//GWT
//        g.setFont(vertexFont);

// testing        
//g.setFillStyle(CssColor.make(255,0,0));
//g.fillText(testString, 5, hoogte - 15);
        
        if (previewModel == null)
            model.paintObject3D(g, shadow, showInside, distance, mat, paintType, retransform);
                                   
                                
                                    
        if (previewModel != null)
        {    previewModel.paintObject3D(g, shadow, showInside, distance, mat, paintType,
                                       retransform);
             //model.fixFacetArray();
             //model.transformBy(mat, true);
        }
        if (helpLine)
        {   
        	//g.setColor(helpLineColor);
            //drawDashedLine(g, pt1.x, pt1.y, pt2.x, pt2.y);
        }    

        if (helpPoint)
        {   //g.setColor(helpPointColor);
        
            //g.drawLine(hp.x, hp.y - POINT / 2, 
            //            hp.x, hp.y + POINT / 2 - 1);
            //g.drawLine(hp.x - POINT / 2, hp.y, 
            //            hp.x + POINT / 2 - 1, hp.y);
                        
                        
        }    
        // outline
        g.setStrokeStyle(Grafiek3DComponent.black);
        if (bordered)
        	g.strokeRect(0, 0, breedte, hoogte);


    } // paint

    public void setPreviewModel(ObjectGroup3D pvModel)
    {   previewModel = pvModel;

    	//repaint();

    }
    
    public void setShowInside(boolean b)
    {   showInside = b;
        //repaint();
    }

    public void setFilled(boolean b)
    {   model.setFilled(b);
        //repaint();
    }    

    public void setDistance(int d)
    {   if (d > MAXDISTANCE)
            distance = MAXDISTANCE;
        else if (d <= minDistance)
            distance = minDistance;
        else
            distance = d;
        //repaint();
    }

    
    public void rotateByZ(double zTheta)
    {   
        mat.zRotateBy(zTheta);        
    }
    
    
    public void rotateBy(double xTheta, double yTheta)
    {   
        mat.xRotateBy(xTheta);        
        mat.yRotateBy(yTheta);
    }

	// werkt wel!
    public void vwRotate(Vector3D v, Vector3D w)
    {   
        mat.vwRotate(v, w);        
        //    repaint();
    }

    
    
    
    public void rotateCake(double xTheta, double yTheta)        
    {
//System.out.println("rotateCake " + xTheta + " " + yTheta);    	
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

    public void zetHoeken(double xAngle, double zAngle)
    {
    	angleX = 0;
    	angleZ = 0;
    	rotateCake(xAngle, zAngle);
    }
    
    public void zetStartHoeken(double xStartAngle, double zStartAngle)
    {
    	angleX = 0;
    	angleZ = 0;
    	angleXStart = xStartAngle;
    	angleZStart = zStartAngle;
    	rotateCake(xStartAngle, zStartAngle);
    }
    
    // shortcuts
    public Object3D objectClicked(int x, int y)
    {   return model.objectClicked(x, y, distance, mat.origin, paintType);
    }
    public Facet3D clickedFacet(int x, int y)
    {   return model.clickedFacet(x, y, distance, mat.origin, paintType);
    }               
    public int facetClicked(int x, int y)
    {   return model.facetClicked(x, y, distance, mat.origin, paintType);
    }               

/*
    public Vector3D vertexClicked(int x, int y)
    {   return model.vertexClicked(x, y, distance, mat.origin);
    }                                      
*/

    public Vector3D[] edgeClicked(int x, int y)
    {   return model.edgeClicked(x, y, distance, mat.origin); 
    }                                      

    public FacetWithVertex facetWithVertexClicked(int x, int y)
    {   return model.facetWithVertexClicked(x, y, distance, mat.origin);
    }                                      
    public FacetWithEdgePoint facetWithEdgePointClicked(int x, int y)
    {   return model.facetWithEdgePointClicked(x, y, distance, mat.origin); 
    }                                      



  
    private double pixDis(int x1, int y1, int x2, int y2)
    {   return Math.sqrt((x1 - x2) * (x1 - x2) +
                         (y1 - y2) * (y1 - y2));
        
    }    
    
    
    
}


