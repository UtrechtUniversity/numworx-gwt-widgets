package fi.doorziengwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class responsible for drawing area 3D Objects since it is familiar with
 * the Contect2d of a Canvas; the class also initializes and
 * manipulates the 3D model, see also class Object3D Container in Grafiek3DGWT<br>
 * Note: help lines and help points are not used since there is no preview  
 * @author huub
 */
public class Object3DContainer //extends JPanel
{       
    // the 3D object(s) in worldspace; previewModel is not used
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

    // for remembering mouse position at start of dragg
    int oldX, oldY;
    // remembering angles
    double angleX, angleY, angleZ;
    // scaling factor for drawing on the canvas
    // pixels per unit world space, incorporates zoomFactor
    double scaleFac;
    // view object from a point at distance VIEWRATIO times its size
    public static int VIEWRATIO = 5;
    // schadows
    boolean shadow = true;
    // showing inside in gray
    boolean showInside = true;
    // background color
    CssColor bgColor = DrawConstants.white;
    
    // distance of view point to screen i.e.
    // distance to z-plane through (model.origin.x, model.origin.y, 0)
    public static double MAXDISTANCE = 100000;
    double minDistance;
    double distance = MAXDISTANCE; // set in paint for perspective
    
    // projections
    public static int CENTRALPROJ = 0;
    public static int PARALLELPROJ = 1;
    
    public int projection = CENTRALPROJ;
    
    double zoomFactor = 9e-1d;
    boolean retransform = true;

    // help lines and help points, not used since there is no preview 
    Vector3D helpStart;
    int pt1x, pt1y;
    int pt2x, pt2y;
    CssColor helpLineColor = null;
    boolean helpLine = false;
    int hpx, hpy;
    CssColor helpPointColor = null;
    boolean helpPoint = false;
    
    // point size
    public static int POINT = 14;
    // dash length in pixels
    public static int DASH = 4;
    
    private boolean bordered = true;

// testing    
public static String testString = "";
public String testString2 = "";

	Context2d context2d;
	int breedte, hoogte;
    
    // constructor
    Object3DContainer(Context2d c2d, int b, int h)
    {   
    	context2d = c2d;
    	breedte = b;
    	hoogte = h;
    	mat = new Matrix3D();
    }

    public void initializeModel(ObjectGroup3D m, boolean newModel)
    {   
        if (newModel)
            mat = new Matrix3D();
        
        model = m;
        
        double wFac = zoomFactor * ((double) breedte) / model.diameter;
        double hFac = zoomFactor * ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);

        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // center of viewing plane corresponds to 
        // center of world model
        mat.setOrigin(((double) breedte) / 2,((double) hoogte) / 2, 0);

        // closer does not yet work well
        minDistance = scaleFac * model.diameter;

        // set initial angles
        if (newModel)
        {
            angleX = 80;
            angleY = 25;        
            angleZ = 25;
            
            mat.zRotateBy(angleZ);
            mat.xRotateBy(angleX);
        }

        repaint();
        
    }
    
    public void setBackground(CssColor c)
    {	if (bordered) 
    		bgColor = DrawConstants.white;
    	else 
    		bgColor = c;
	}
    
    public void setBordered(boolean b)
    {	bordered = b;
    	if (!b) 
    	{	//bgColor = getBackground();
    	
    	}
    	else 
    		bgColor = DrawConstants.white;
    
    	repaint();
    }
    
    

    public void setProjection(int proj)
    {   if (proj == CENTRALPROJ)
            projection = CENTRALPROJ;
        if (proj == PARALLELPROJ)
            projection = PARALLELPROJ;    
        repaint();

    }
    public void setZoomFactor(double factor)
    {   zoomFactor = factor;
        double wFac = zoomFactor * ((double) breedte) / model.diameter;
        double hFac = zoomFactor * ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);

        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // closer does not yet work well
        minDistance = scaleFac * model.diameter;
        
        repaint();
    }    

    public void resetModel()
    {   
    	if(model==null) return;
        
        double wFac = zoomFactor * ((double) breedte) / model.diameter;
        double hFac = zoomFactor * ((double) hoogte) / model.diameter;
        scaleFac = Math.min(wFac, hFac);

        // make sure we are viewing from z+ thus y+ to the top
        // x+ to the right
        mat.setScale(scaleFac, - scaleFac, scaleFac);
        // center of viewing plane corresponds to origin (0, 0, 0)
        // of world model
        mat.setOrigin(((double) breedte) / 2,((double) hoogte) / 2, 0);
        repaint();
    }

    public void repaint()
    { 	paint(context2d);
    }
    
    public void paint(Context2d g)
    {   

		if (model == null)
		{	// outline only
    	    g.setStrokeStyle(DrawConstants.black);
	        if (bordered) 
	        {  	g.beginPath();
	        	g.strokeRect(0, 0, breedte, hoogte);
	        }
			return;
        }
        // set distance in view space
        // object size is model.diameter * scaleFac
        // multiply by VIEWRATIO
        if (projection == CENTRALPROJ)
            distance = model.diameter * scaleFac * VIEWRATIO;// zoomFactor;
        if (projection == PARALLELPROJ)    
            distance = MAXDISTANCE;
        
        // background
        g.setFillStyle(bgColor);
        g.fillRect(0, 0, breedte, hoogte);
        
        if (previewModel == null)
        {   model.paintObject3D(g, shadow, showInside, distance, mat, paintType, retransform);
        
        }
                                    
        if (previewModel != null)
        {    previewModel.paintObject3D(g, shadow, showInside, distance, mat, paintType, retransform);
        
        }
        
        if (helpLine)
        {   g.setStrokeStyle(helpLineColor);
        	drawDashedLine(g, pt1x, pt1y, pt2x, pt2y);
        }    

        if (helpPoint)
        {   g.setStrokeStyle(helpPointColor);
        
            g.beginPath();
            g.moveTo(hpx, hpy - POINT / 2);
            g.lineTo(hpx, hpy + POINT / 2 - 1);
            g.stroke();
            
            g.beginPath();
            g.moveTo(hpx - POINT / 2, hpy);
            g.lineTo(hpx + POINT / 2 - 1, hpy);
            g.stroke();
            
                        
        }    
        // outline
        g.setStrokeStyle(DrawConstants.black); // gray!!
        if (bordered)
        {	        
        	g.beginPath();
        	g.strokeRect(0,0,breedte, hoogte);
        }

if (!testString2.equals(""))
{	
g.setFillStyle(DrawConstants.red);
g.fillText(testString2,10,15);
}
    }

    public void setPreviewModel(ObjectGroup3D pvModel)
    {   previewModel = pvModel;
        repaint();
    }
    
    public void setShowInside(boolean b)
    {   showInside = b;
        repaint();
    }

    public void setFilled(boolean b)
    {   model.setFilled(b);
        repaint();
    }    

    public void setDistance(int d)
    {   if (d > MAXDISTANCE)
            distance = MAXDISTANCE;
        else if (d <= minDistance)
            distance = minDistance;
        else
            distance = d;
        repaint();
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

    public void vwRotate(Vector3D v, Vector3D w)
    {   
        mat.vwRotate(v, w);        
        repaint();
    }

    public void rotateCake(double xTheta, double yTheta)        
    {
         angleX += xTheta;
         angleY += yTheta;
         if (angleX > 80)
            angleX = 80;
         if (angleX < 0)
            angleX = 0;
         mat.reset();                    
         mat.zRotateBy(angleY);                    
         mat.xRotateBy(angleX);       
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

    public Vector3D[] edgeClicked(int x, int y)
    {   return model.edgeClicked(x, y, distance, mat.origin); 
    }                                      

    public FacetWithVertex facetWithVertexClicked(int x, int y)
    {   return model.facetWithVertexClicked(x, y, distance, mat.origin);
    }                                      
    public FacetWithEdgePoint facetWithEdgePointClicked(int x, int y)
    {   return model.facetWithEdgePointClicked(x, y, distance, mat.origin); 
    }                                      

    public void hideHelpLine()
    {   helpLine = false;
        repaint();
       
    }    
    
    public void showHelpLine(Vector3D v, int px, int py, CssColor c)
    {   helpLine = true;
        helpStart = new Vector3D(v);
        Vector3D trV = mat.transform(Vector3D.minus(helpStart, model.center));
        double temp =  distance / (distance - trV.z);
        pt1x = (int) Math.round((mat.origin.x + (trV.x - mat.origin.x) * temp));
        pt1y = (int) Math.round((mat.origin.y + (trV.y - mat.origin.y) * temp));
        pt2x = px;
        pt2y = py;
        helpLineColor = c;
        repaint();
       
    }    
    public void updateHelpLine(int px, int py)
    {   if (helpLine)
        {
            Vector3D trV = mat.transform(Vector3D.minus(helpStart, model.center));
            double temp =  distance / (distance - trV.z);
            pt1x = (int) Math.round((mat.origin.x + (trV.x - mat.origin.x) * temp));
            pt1y = (int) Math.round((mat.origin.y + (trV.y - mat.origin.y) * temp));
            pt2x = px;
            pt2y = py;

            repaint();
           
        }
    }    

    
    public void hideHelpPoint()
    {   helpPoint = false;
        repaint();
    }    
    
    public void showHelpPoint(int px, int py, CssColor c)
    {   helpPoint = true;
        hpx = px;
        hpy = py; 
        helpPointColor = c;
        repaint();
    }    
    
    
    public void updateHelpPoint(int px, int py)
    {   if (helpPoint)
        {
    		hpx = px;
    		hpy = py;
    		repaint();
        }
    }    
    
    public void drawDashedLine(Context2d g, double x1, double y1, double x2, double y2)
    {   int dis = (int) Math.round(pixDisD(x1, y1, x2, y2));
    
        boolean dashOn = true;
        double xStart, xEnd, yStart, yEnd;
        // points too close to dash
        if (dis <= DASH)
        {   
        	g.beginPath();
        	g.moveTo(x1, y1);
        	g.lineTo(x2, y2);
        	g.stroke();
        }    
        // vertical line
        else if (x1 == x2)
        {   xStart = x1;
            if (y1 < y2)
            {   yStart = y1; 
                yEnd = y2;
            }
            else
            {   yStart = y2;
                yEnd = y1;
            }    
            while (yStart < yEnd)
            {   if (dashOn)
                {   
                	g.beginPath();
            		g.moveTo(xStart, yStart);
            		g.lineTo(xStart, Math.min(yStart + DASH, yEnd));
            		g.stroke();
                
                    dashOn = false;        
                }
                else
                {   dashOn = true;        
                }    
                yStart += DASH;                
            }
        }
        // horizontal line
        else if (y1 == y2)
        {   yStart = y1;
            if (x1 < x2)
            {   xStart = x1; 
                xEnd = x2;
            }
            else
            {   xStart = x2;
                xEnd = x1;
            }    
            while (xStart < xEnd)
            {   if (dashOn)
                {   
                	g.beginPath();
                	g.moveTo(xStart, yStart);
                	g.lineTo(Math.min(xStart + DASH, xEnd), yStart);
                	g.stroke();
                
                
                    dashOn = false;        
                }
                else
                {   dashOn = true;        
                }    
                xStart += DASH;                
            }
        }
        // sloped line
        else
        {   if (x1 < x2)
            {   xStart = x1; 
                xEnd = x2;
                yStart = y1;
                yEnd = y2;
            }
            else
            {   xStart = x2;
                xEnd = x1;
                yStart = y2;
                yEnd = y1;
            }    
            // find slope of segment
            double slope = ((double) (yEnd - yStart))/(xEnd - xStart);
            double alpha = Math.atan(slope);            
            double length = pixDisD(xStart, yStart, xEnd, yEnd);
            int steps = (int) Math.round(
                pixDisD(xStart, yStart, xEnd, yEnd) / DASH);
                
            for (int i = 1; i <= steps; i++)
            {   double xFrom = xStart + Math.cos(alpha) * DASH * (i - 1);
                double yFrom = yStart + Math.sin(alpha) * DASH * (i - 1);
                double xTo = xStart + Math.cos(alpha) * DASH * i;
                double yTo = yStart + Math.sin(alpha) * DASH * i;
                    
                xTo = Math.min(xTo, xEnd);    
                if (yStart > yEnd)
                    yTo = Math.max(yTo, yEnd);
                else
                    yTo = Math.min(yTo, yEnd);
                    
                    
                if ((i % 2) != 0)
                {   
                	g.beginPath();
                	g.moveTo(xFrom, yFrom);
                	g.lineTo(xTo, yTo);
                	g.stroke();
                
                }
            }
            
        }    
    }
    
    private double pixDisD(double x1, double y1, double x2, double y2)
    {   return Math.sqrt((x1 - x2) * (x1 - x2) +
                         (y1 - y2) * (y1 - y2));
        
    }    
    
    
    
}


