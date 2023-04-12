package fi.grafiek3dgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * a facet of a 3D object, i.e. a polygon with its points located in some plane of 3-space (the world space);
 * at construction, the points of the facet are specified by giving the indices of the vertices (vectors in 3-space)
 * in the the vertex array of the 3D object to which the facet belongs (see class Object3D). Thus when calling the
 * constructor, the vertex array of the 3D object to which this facet belongs must be have been created and filled with vertices; <br>
 * if the Facet3D belongs to a convex Object3D, make sure the vertices of the Facet3D are listed clockwise when viewing the Object3D 
 * from the outside, in order to obtain correct fill colors for the Facet3D, when the inside of the Facet3D should be painted in gray; <br>
 * the vertices of the 3D object and thus of all its facets have transformed coordinated in view space;
 * the transformed coordinates of the 3D object are not available at construction of the facet, but are calculated when  
 * needed by calling the method updateTrPoints(); if the 3D object's points in world space are changed, the facet is
 * updated using method updatePoints(); <br>
 * for a Facet3D drawing of the edges or the inside can be skipped; if the inside of a Facet3D is drawn, facets which are 
 * "behind" the Facet3D will not be visible, if only the edges of the Facet3D are drawn, edges and/or the inside of facest 
 * which are "behind" the Facet3D will be drawn in "hidden colors", derived from their basic colors, see method
 * paintFacet3D()  
 * @author huub
 */
public class Facet3D 
{   
	public static final CssColor darkGreen = CssColor.make(41, 156, 57);
    public static final CssColor mediumGreen = CssColor.make(173, 222, 99);
    public static final CssColor brownRed = CssColor.make(214, 0, 0);
    public static final CssColor lightRed = CssColor.make(255, 156, 74);
    public static final CssColor mediumBlue = CssColor.make(99, 198, 222);
    public static final CssColor lightGray = CssColor.make(192,192,192); 
    public static final CssColor black = CssColor.make(0,0,0);
    public static final CssColor blue = CssColor.make(0,0,255);    
    public static final CssColor red = CssColor.make(255,0,0);
    public static final CssColor orange = CssColor.make(255,200,0);
    
    /**
     * the number of 3D points in the facet
     */
    public int numPoints;
    /**
     * indices of the 3D points in the arrays of (transformed) vertices of the 3D object to which this facet belongs
     */
    public int[] indices;
    /**
     * the 3D real points of the facet, world space
     */
    public Vector3D[] points;
    /**
     * the transformed 3D real points of the facet, view space
     */
    public Vector3D[] trPoints;
    /**
     * individual vertices can be drawn in a different manner by coding them, see method paintFacet3D 
     */
    public int[] vertexCodes;        
    /**
     * individual edges can be drawn in a different manner by coding them, see method drawPolygon 
     */
    public int[] edgeCodes;   
    /**
     * labels for the vertices; construct for the 3D object to which the facet belongs an array with vertex labels and
     * copy these labels into the facets in the same way as the points (the indices of the labels in the vertex label array are known); <br>
     * note that the vertex labels of 3D object can only be drawn by means of drawing the facets. 
     */
    public String[] vertexLabels;
    /**
     * normal vector of the Fact3D world space
     */
    public Vector3D normal;
    /**
     * normal vector of length 1 of the transformed Facet3D in view space
     */
    public Vector3D unitNormal;
    /**
     * the basic color of the inside of the Facet3D (if drawn), see fillColor
     */
    public CssColor color;
    /**
     * should the edges of the Facet3D be drawn?
     */
    public boolean outlined = true;
    /**
     * should the inside of the Facet3D be drawn? if not, the Facet3D is still clickable  
     */
    public boolean filled = true;
    /**
     * true if neither outlined nor filled; an empty Facet3D is still clickable if visible == true(!) 
     */
    public boolean empty = false;
    /**
     * is the Facet3D visible? if not, the Facet3D is not clickable
     */
    public boolean visible = true;
    /**
     * the color of the edges of the Facet3D 
     */
    public CssColor outlineColor = CssColor.make(0,0,0);
    // the actual fill color used (thus adapted for shadow etc)
    /**
     * the actual color of the inside of the Facet3D, that is "color" adapted for shadow etc. 
     */
    public CssColor fillColor;
    /**
     *  how to draw "hidden" outlines when the facets of an Object3D are  not filled:
     *  0 = lighter, 1 = dashed 2 = dashed and lighter; to change this mode, rebuild the Object3D 
     */
    public int hiddenOutlineMode = 0; 
    
    /**
     * Doorzien: basic (non-hidden) color for lines 
     */
    public CssColor lineColor = blue;
    /**
     * Doorzien: basic (non-hidden) color for the edges of a plane 
     */
    public CssColor planeOutlineColor = brownRed;    
    /**
     * Doorzien: basic (non-hidden) color for points 
     */
    public CssColor pointColor = darkGreen;
    /**
     * possible edge colors
     */
    public CssColor[] edgeColors =
		{outlineColor, lineColor, planeOutlineColor, pointColor};
    /**
     * average height above x-y-plane after transforming to view space; see method paintObject3D() in class Object3D
     */
    public double zValue;
    /**
     * the barycenter of the transformed Facet3D in view space
     */
    public Vector3D barycenter;
    /**
     * Doorzien: drawing a line on a Feacet3D or cutting an Object3D with a plane subdivides the facets involved;
     * if a Facet3D is a subdivision, remember the "parent" Facet3D  
     */
    Facet3D isReplacementOf;
    /**
     * dash length in pixels
     */
    public static int DASH = 4;
    /**
     * thickened vertex size
     */
    public static int BIGPOINT = 7;
    /**
     * normal vertex size
     */
    public static int SMALLPOINT = 4;
    /**
     * should the vertices be thickened? 
     */
    boolean thickenVertices = false;
    /**
     * Doorzien: each edge can contain additional points: "ticks";
     * numTicks is the number of ticks for each edge 
     */
    int[] numTicks;
    /**
     * Doorzien: for each edge, the first tick (if any) in world space
     */
    Vector3D[] tickStart;
    /**
     * Doorzien: for each edge, if numTicks is larger then 1, the second tick on the edge; subsequent ticks can then be calculated from 
     * tickStart and tickStep; see method findTransformedTickmarks()
     */
    Vector3D[] tickStep;
    /**
     * should the ticks on the individual edges be drawn?
     */
    boolean[] drawTicks; 
    /**
     * are ticks visible?
     */
    boolean ticksVisible = false;
    /**
     * color of the ticks when non-hidden
     */
    public CssColor tickColor = darkGreen;
    /**
     * color of the ticks when hidden
     */
    public CssColor hiddenTickColor = mediumGreen;
    /**
     * should the vertex labels be drawn? in Doorzien the vertex labels of the basic 3d-objects are letters, more letters are
     * added whenever new points are added to the 3d-object; see also class Axes   
     */
    boolean letters = false;
    /**
     * the color of the vertex labels
     */
    public CssColor letterColor = CssColor.make(0,0,0);
    /**
     * is this Facet3D a facet (segment) of a coordinate axis? see class Axes 
     */
    boolean isOnAxis = false;
    
    /**
     * constructor
     * @param pts the array containing all the vertices of the Object3D to which this Facet3D will belong
     * @param inds the array containing the indices of the vertices in pts which should be part of this Facet3D
     * @param c the color of this Facet3D when filled
     */
    public Facet3D(Vector3D[] pts, int[] inds, CssColor c)
    {   // init indices
        indices = inds; 
        // get the number of points in the facet as the length of indices
        numPoints = indices.length;
        // note the 2 times new
        points = new Vector3D[numPoints];
        trPoints = new Vector3D[numPoints];
        // initialized as zero
        vertexCodes = new int[numPoints];
        // initialized as zero
        edgeCodes = new int[numPoints];
        // initialized as null
        vertexLabels = new String[numPoints];
        // initialized as null        
        tickStart = new Vector3D[numPoints];
        // initialized as null        
        tickStep = new Vector3D[numPoints];
        // initialized as zero
        numTicks = new int[numPoints];
        // initialized as false
        drawTicks = new boolean[numPoints];
        // get the vertices of the Facet3D
        for (int i = 0; i < numPoints; i++)
        {   // pts[indices[i]] is the Vector3D we need
            points[i] = pts[indices[i]];
            // temporary
            trPoints[i] = points[i];            
        }
        // set fill color
        color = c;
        checkDoublePoints();
        if (numPoints < 3)
            filled = false;
        setNormal();
        // is calculated after transforming normal
        unitNormal = new Vector3D();
    }
    /**
     * update the points of the Facet3D when the vertices of the object have been changed  
     * @param pts the vertex array of the object to which the Facet3D belongs
     */
    public void updatePoints(Vector3D[] pts)
    {   for (int i = 0; i < numPoints; i++)
            points[i] = pts[indices[i]];
        setNormal();    
    }
    /**
     * update the transformed points of the Facet3D when the transformed vertices of the object have been changed  
     * @param trPts the transformed vertex array of the object to which the Facet3D belongs
     */
    public void updateTrPoints(Vector3D[] trPts)
    {   for (int i = 0; i < numPoints; i++)
            trPoints[i] = trPts[indices[i]];
    }
    /**
     * check if two consecutive points are identical, if yes, delete one of them; <br>
     * "double points" can occur in the case of parametrized surfaces and cause problems 
     * when calculating the normal vector   
     */
    public void checkDoublePoints()
    {   for (int i = 0; i < numPoints; i++)
        {   if (Vector3D.equals(points[i], points[(i + 1) % numPoints]))
                deletePoint(i);
        }
    }
    /**
     * delete the facet-point at points[index] 
     * @param index index of point to be deleted
     */
    private void deletePoint(int index)
    {   for (int i = index + 1; i < numPoints-1; i++)
        {   indices[i] = indices[i + 1];
            points[i] = points[i + 1];
        }
        numPoints--;
    }
    /**
     * find the normal vector of the Facet3D in world space
     */
    public void setNormal()
    {   // point
        if (numPoints <= 1)
            normal = new Vector3D(); // zero vector
        // directed segment    
        else if (numPoints == 2)
            normal = Vector3D.minus(points[1], points[0]);
        // directed polygon    
        else
        {   // find first 2 directional vectors in facet plane
            Vector3D v1 = Vector3D.minus(points[1], points[0]);
            Vector3D v2 = Vector3D.minus(points[2], points[0]);
            int index = 3;
            // check for more if multiples are encountered
            while (Vector3D.isMultipleOf(v1, v2) && (index < numPoints))
            {   v2 = Vector3D.minus(points[index], points[0]);
                index++;
            }
            normal = Vector3D.crossProduct(v1, v2); // which could be the 0-vector
        }
    }
    /**
     * replace the normal vector by its opposite
     */
    public void reverseNormal()
    {
    	normal = new Vector3D(-normal.x, -normal.y, -normal.z);
    }
    /**
     * average z-coordinate of the transformed vertices of this Facet3D corrected for
     * viewing from (origin.x,origin.y,distance) 
     * @param origin origin of view space
     * @param distance distance for viewing in view space (on the positive z-axis)
     */
    public void calculateZValue(Vector3D origin, double distance)
    {   // find barycenter of transformed Facet3D
        double bX = 0;
        double bY = 0;        
        double bZ = 0;                
        for (int i = 0; i < numPoints; i++)
        {   bX += trPoints[i].x;
            bY += trPoints[i].y;
            bZ += trPoints[i].z;
        }
        Vector3D bC = new Vector3D(bX / numPoints, bY / numPoints, bZ / numPoints);
        Vector3D eye = new Vector3D(origin.x, origin.y, distance);    
        zValue = distance - Vector3D.distance(bC, eye);    
    }
    /**
     * calculate the attribute barycenter of the Facet3D
     */
    public void calculateBarycenter()
    {   double xSum = 0;
        double ySum = 0;
        double zSum = 0;
        for (int i = 0; i < numPoints; i++)
        {   xSum += trPoints[i].x;
            ySum += trPoints[i].y;
            zSum += trPoints[i].z;
        }
        barycenter = new Vector3D(
            xSum / numPoints, ySum / numPoints, zSum / numPoints);
    }
    /**
     * view space: project the transformed facet on the plane z = 0 from
     * distance = d (positive double) in the positive direction of the z-axis,
     * thus from the view point (o.x, o.y, d)
     * @param d the distance from o on the positive z-axis
     * @param o the origin of the view space
     * @return the projected Facet3D as a Polygon (integer coordinates)
     */
    public Polygon project(double d, Vector3D o)
    {   // find projection Polygon
        int nPoints = numPoints;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        for (int i = 0; i < numPoints; i++)
        {   // view space, origin at (o.x, o.y, o.z)
            // xy-projection
            // project point on plane through (o.x, o.y, 0)
            double temp =  d / (d - trPoints[i].z);
            xPoints[i] = (int) Math.round((o.x + (trPoints[i].x - o.x) * temp));
            yPoints[i] = (int) Math.round((o.y + (trPoints[i].y - o.y) * temp));
        }
        return new Polygon(xPoints, yPoints, nPoints);
    }
    /**
     * view space: project the transformed facet on the plane z = 0 from
     * distance = d (positive double) in the positive direction of the z-axis,
     * thus from the view point (o.x, o.y, d)
     * @param d the distance from o on the positive z-axis
     * @param o the origin of the view space
     * @return the projected Facet3D as a Polygon2D (real coordinates)
     */
    public Polygon2D project2D(double d, Vector3D o)
    {   // find projection Polygon2D
        int nPoints = numPoints;
        double[] xPoints = new double[numPoints];
        double[] yPoints = new double[numPoints];
        for (int i = 0; i < numPoints; i++)
        {   // view space, origin at (o.x, o.y, o.z)
            // xy-projection
            // project point on plane through (o.x, o.y, 0)
            double temp =  d / (d - trPoints[i].z);
            xPoints[i] = (o.x + (trPoints[i].x - o.x) * temp);
            yPoints[i] = (o.y + (trPoints[i].y - o.y) * temp);
        }
        return new Polygon2D(xPoints, yPoints, nPoints);
    }
   /**
     * view space: project the points in the array points on the plane z = 0 from
     * distance = d (positive double) in the positive direction of the z-axis,
     * thus from the view point (o.x, o.y, d)
     * @param points the 3D-points in view space to project
     * @param d the distance from o on the positive z-axis
     * @param o the origin of the view space
     * @return the projections 
     */
    public Point[] projectPoints(Vector3D[] points, double d, Vector3D o)
    {   Point[] result = new Point[points.length];
        for (int i = 0; i < points.length; i++)
        {   // view space, origin at (o.x, o.y, o.z)
            // xy-projection
            // project point on plane through (o.x, o.y, 0)
            double temp =  d / (d - points[i].z);
            int x = (int) Math.round((o.x + (points[i].x - o.x) * temp));
            int y = (int) Math.round((o.y + (points[i].y - o.y) * temp));
            result[i] = new Point(x, y);
        }
        return result;
    }
    
    /**
     * paint this Facet3D; determine fill color, and if this Facet3D is
     * not filled, the edge colors; take care of thickened vertices,
     * tickmarks and letters at the vertices (if any) 
     * @param g Context2d for drawing
     * @param shadow should shadow colors be used?
     * @param inside should the inside of the facet be painted in grey
     * @param dis viewing distance
     * @param mat transforming world space to view space
     * @param ob redundant 
     */
    public void paintFacet3D(Context2d g, boolean shadow, boolean inside, 
                             double dis, Matrix3D mat, Object3D ob)
    {
        
        Polygon2D p = project2D(dis, mat.origin);

        // the transformed facet is visible from (origin.x, origin.y, d) if 
        // 	(origin.x, origin.y, d) is at the side of the transformed
        // facet from which the normal vector points out
        // the transformed facet is of the vector form
        // trPoints[0] + "some plane through (0, 0, 0)" and that plane
        // has normal vector unitNormal, so:
        // find the angle between (origin.x, origin.y, d) minus trPoints[0]
        // and unitNormal

        Vector3D eye = new Vector3D(mat.origin.x, mat.origin.y, dis);
        // work with normal support here
        Vector3D support = new Vector3D(trPoints[0]);
        eye = Vector3D.minus(eye, support);
        Vector3D.makeUnitary(eye);
        boolean visFromD = Vector3D.dotProduct(eye, unitNormal) >= -Vector3D.NZero;
        
        // determine the fill color of the facet
        // and fill
        // inside = false gives same color as outside with correct shadow 
        // via -unitNormal
        if (inside && shadow)
        {   if (visFromD)
    		{   
    			g.setFillStyle(Facet3D.shadowColor(this, false));
    			fillColor = Facet3D.shadowColor(this, false);
    		}
        	else
        	{   
        		g.setFillStyle(Facet3D.shadowGrayColor(this));
        		fillColor = Facet3D.shadowGrayColor(this);
        	}
        }
        else if (!inside && shadow)
        {   if (visFromD)
        	{    
    			g.setFillStyle(Facet3D.shadowColor(this, false));
    			fillColor = Facet3D.shadowColor(this, false);
        	}
        	else
        	{   
        		g.setFillStyle(Facet3D.shadowColor(this, true));
        		fillColor = Facet3D.shadowColor(this, true);
        	}
        }
        else if (inside && !shadow)
        {   if (visFromD)
    		{   
    			g.setFillStyle(color);
    			fillColor = color;
    		}
        	else
        	{   
        		g.setFillStyle(lightGray);
        		fillColor = lightGray;
        	}
        }
        else // none
        {   
        	g.setFillStyle(color);
        	fillColor = color;
        }

        if (filled && (numPoints >= 3))
        {           
    		g.moveTo(p.xpoints[0], p.ypoints[0]);
    		g.beginPath();
    		for (int k = 1; k < p.npoints; k++)
    		{	g.lineTo(p.xpoints[k], p.ypoints[k]);
    		}
    		g.lineTo(p.xpoints[0], p.ypoints[0]);
    		g.closePath();
    		g.fill();
        
        }

        // determine outline color and draw outline
        if (outlined)
        {    
             // facet filled, no edge colors overidden
             if (filled && !edgesLabeled())
             {   // for lines change outlineColor externally
            	 if (visFromD)             
                 {   
                	 g.setStrokeStyle(outlineColor);
                 }
                 else
                 {   
                	 g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                 }                    
             }
             // facet transparent, no edge colors overridden 
             else if (!filled && !edgesLabeled())
             {   
            	 
            	 if (numPoints < 3)
            	 {	
            	 	g.setStrokeStyle(outlineColor);
            	 }
            	 else if (!visFromD && (hiddenOutlineMode % 2 == 0))
                 {   
                	 g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                 }
                 else    
                 {   
                	 g.setStrokeStyle(outlineColor);
                 }
             }    
             
             // some edge colors overridden
             if (edgesLabeled() || (hiddenOutlineMode != 0))
             {    drawPolygon(g, p, visFromD);
             
             }
             else  
             {   if (numPoints < 3) 
             	 {	            	 
            	 	if (numPoints > 1)
            	 	{	
            	 		g.beginPath();
            	 		g.moveTo(p.xpoints[0], p.ypoints[0]);
            	 		g.lineTo(p.xpoints[1], p.ypoints[1]);
            	 		g.stroke();
            	 	}	
             	 }
            	 else
            	 {	 
            		 g.moveTo(p.xpoints[0], p.ypoints[0]);
            		 g.beginPath();
            		 for (int k = 1; k < p.npoints; k++)
            		 {	g.lineTo(p.xpoints[k], p.ypoints[k]);
            		 }
            		 g.lineTo(p.xpoints[0], p.ypoints[0]);
            		 g.closePath();
            		 g.stroke();
            	
            	}
             
             }
        } // if (outlined)
         
        
        if (thickenVertices)
        {	
        	// draw thickened points if any
        	for (int cnt = 0; cnt < numPoints; cnt++)
        	{   // -1 is not thickened
        		if (vertexCodes[cnt] >= 0)
        		{    //boolean hidden = false;
        			boolean highlighted = false;
        			// hiding through normal
        			if ((vertexCodes[cnt] >= 0) && (vertexCodes[cnt] < 10))
        			{
        				// use redundancy here
        				// edgeColors[vertexCodes[0]] is the global outlineColor
        				// edge "hidden"
        				if (numPoints < 3)
        				{	
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt]]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt]]);
        				
        				}
                        if (!visFromD && (hiddenOutlineMode % 2 == 0)
                        	)
                        {   
                        	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt]]));
                        	g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt]]));
                        }        
                        else // not "hidden"
                        {   
                        	g.setStrokeStyle(edgeColors[vertexCodes[cnt]]);
                        	g.setFillStyle(edgeColors[vertexCodes[cnt]]);
                        }
        				    
        			}
        			// externally hidden
        			else if ((vertexCodes[cnt] >= 10) && (vertexCodes[cnt] < 20))
        			{   if (hiddenOutlineMode % 2 == 0)
                	 	{   
              	 	  		g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 10]));
              	 	  		g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 10]));
                	 	}
        				else // no "hidden" color
        				{   
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 10]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt] % 10]);
        				}
                     	    
        			}    
        			// hightlighted and hiding through normal
        			else if ((vertexCodes[cnt] >= 20) && (vertexCodes[cnt] < 30))
        			{   highlighted = true;
        				if (!visFromD && (hiddenOutlineMode % 2 == 0))
        				{   
        					g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 20]));
        					g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 20]));
        				}        
        				else // not "hidden"
        				{   
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 20]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt] % 20]);
        				}
    
        			}
        			// highlighted and externally hidden
        			else if ((vertexCodes[cnt] >= 30) && (vertexCodes[cnt] < 40))
        			{   
        				if (hiddenOutlineMode % 2 == 0)
                        {   
                       	 	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 30]));
                       	 	g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 30]));
                        }
                        else // no "hidden" color
                        {   
                       	 	g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 30]);
                       	 	g.setFillStyle(edgeColors[vertexCodes[cnt] % 30]);
                        }
        				highlighted = true;

        			}    
        			// extern unhidden
        			else if ((vertexCodes[cnt] >= 40) && (vertexCodes[cnt] < 50))
        			{   
                   	 	g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 40]);
                   	 	g.setFillStyle(edgeColors[vertexCodes[cnt] % 40]);
                    }    
        			if (highlighted)
        			{	
        				g.beginPath();
        				g.fillRect(p.xpoints[cnt] - BIGPOINT / 2,
                                p.ypoints[cnt] - BIGPOINT / 2, 
                                BIGPOINT, BIGPOINT);
        			}	
        			else
        			{
        				g.fillRect(p.xpoints[cnt] - SMALLPOINT / 2,
                             p.ypoints[cnt] - SMALLPOINT / 2, 
                             SMALLPOINT, SMALLPOINT);
        			}               
        		} // if (vertexCodes[i] >= 0)  
        	} // for
        } // if (thickenVertices)	


        // drawing tickmarks
        if (ticksVisible)
        {   if (visFromD)
    		{   
    			g.setStrokeStyle(tickColor);
    		}
        	else	    
        	{   
        		g.setStrokeStyle(hiddenTickColor);
        	}
            for (int eCnt = 0; eCnt < numPoints; eCnt++)
            {   if (drawTicks[eCnt] && (numTicks[eCnt] > 0))
                {   Vector3D[] trTicks = findTransformedTicks(eCnt);
                    Point[] projTicks = projectPoints(trTicks, dis, mat.origin);
                    for (int pCnt = 0; pCnt < projTicks.length; pCnt++)
                    {
                         g.beginPath();
                         g.arc(projTicks[pCnt].x, projTicks[pCnt].y, SMALLPOINT / 2, 0, 2 * Math.PI);
                         
                    }
                }    
            }
        }
       
        // drawing letters        
        if (letters)
        {   
        	
        	g.setStrokeStyle(black);
        	g.setFillStyle(black);
        	            
            String fontString = "10px bold sans-serif";
    		g.setFont(fontString);
        	    		
    		TextMetrics tm = g.measureText("XX");
    		double letterBreedte = tm.getWidth();
            
            for (int vCnt = 0; vCnt < p.npoints; vCnt++)
            {
                
                if ((vertexLabels[vCnt] != null))
                {
                	if (isOnAxis && !vertexLabels[vCnt].equals(""))
                	{
                		g.beginPath();
                		g.arc(p.xpoints[vCnt], p.ypoints[vCnt], SMALLPOINT / 2, 0, 2 * Math.PI);
                		g.fill();
                	
                	}
                	if (!vertexLabels[vCnt].equals("") &&
                		!vertexLabels[vCnt].equals("XX"))
                	{	
                	
                		TextMetrics tmv = g.measureText(vertexLabels[vCnt]);
                		double labelBreedte = tm.getWidth();
                		double labelHoogte = 6;
                		
                		String vertexLabel = vertexLabels[vCnt];
                		boolean floorLabel = false;
                		if (vertexLabels[vCnt].charAt(0) == 'F')
                		{	floorLabel = true;
                			vertexLabel = vertexLabels[vCnt].substring(1);
                		
                		}
                		double bx = p.xpoints[vCnt];
                		double by = p.ypoints[vCnt];
                		double ox = mat.origin.x;
                		double oy = mat.origin.y;
                		double shiftX = ox - bx;
                		double shiftY = oy - by;
                		if ((shiftX == 0) && (shiftY >= 0))
                		{	bx += 7;
                			by += 3;
                		}
                		else if ((shiftX == 0) && (shiftY < 0))
                		{	bx += 7;
                			by += 3;
                		}
                		else if (!floorLabel && (shiftX > 0) && (shiftY >= 0))
                		{	
                			bx -= labelBreedte / 2;	
                    		
                			by += labelHoogte;
                		}
                		else if (!floorLabel && (shiftX < 0) && (shiftY >= 0))
                		{	bx += 3;
                    		
                			by += labelHoogte;
                		}
                		else if (!floorLabel && (shiftX > 0) && (shiftY < 0))
                		{	
                			bx -= labelBreedte / 2;
                    		
                			by += labelHoogte + 3;
                		}
                		else if (!floorLabel && (shiftX < 0) && (shiftY < 0))
                		{	
                			bx -= labelBreedte / 2;
                    		
                			by += labelHoogte + 3;
                    	}
                    	else if (floorLabel && (shiftX > 0) && (shiftY >= 0))
                    	{	
                    		bx -= labelBreedte / 2;
                    		by -= 3;
                    	}
                    	else if (floorLabel && (shiftX < 0) && (shiftY >= 0))
                    	{	
                    		bx = bx - labelBreedte / 2 + 3;
                    		by -= 3;
                    	}
                    	else if (floorLabel && (shiftX > 0) && (shiftY < 0))
                    	{	
                    		bx -= labelBreedte / 2;
                    		
                    		by += labelHoogte + 3;
                    	}
                    	else if (floorLabel && (shiftX < 0) && (shiftY < 0))
                    	{	
                    		bx = bx - labelBreedte / 2 + 3;	
                    		
                    		by += labelHoogte + 3;
                    	}
						g.fillText(vertexLabels[vCnt], bx, by);
                    
                	}
                    
                } //if ((vertexLabels[vCnt] != null))
            } // for
        } // if (letters)
    }  // paintFacet  

    /**
     * for the edge with index edgeIndex find the tickmarks
     * in view space
     * @param edgeIndex index of edge
     * @return Vector3D[] containing the tickmarks of the edge in view space
     */
    public Vector3D[] findTransformedTicks(int edgeIndex)
    {   // contains at least 1 point
        Vector3D[] result = new Vector3D[numTicks[edgeIndex]];
        // edge in world space
        Vector3D worldEdgeStart = points[edgeIndex];
        Vector3D worldEdgeEnd = points[(edgeIndex + 1) % numPoints];
        // edge in view space
        Vector3D viewEdgeStart = trPoints[edgeIndex];
        Vector3D viewEdgeEnd = trPoints[(edgeIndex + 1) % numPoints];
        // tick start world space
        Vector3D worldTickStart = tickStart[edgeIndex];
        // vector edgeStart -> edgeEnd, world space
        Vector3D edgeDirection = Vector3D.minus(worldEdgeEnd, worldEdgeStart);
        // vector edgeStart -> tickStart (first tick), world space
        Vector3D startDirection = Vector3D.minus(worldTickStart, worldEdgeStart);     
        // ratio in world space
        double lambda = Vector3D.length(startDirection) /
                        Vector3D.length(edgeDirection);
        // vector edgeStart -> edgeEnd, view space                        
        Vector3D trEdgeDirection = Vector3D.minus(viewEdgeEnd, viewEdgeStart);                        
        Vector3D trStartDirection = new Vector3D(trEdgeDirection);
        // vector edgeStart -> tickStart (first tick), view space
        Vector3D.scaleBy(trStartDirection, lambda);
        // tickStart (first tick), view space
        Vector3D viewTickStart = Vector3D.plus(viewEdgeStart, trStartDirection);
        result[0] = viewTickStart;        
        // fix other tick marks (if any)
        if (numTicks[edgeIndex] > 1)
        {   // create tickStep (second tickmark)
            Vector3D worldTickStep = tickStep[edgeIndex];            
            // vector tickStart -> tickStep, world space
            Vector3D stepDirection = Vector3D.minus(worldTickStep, worldTickStart);        
            // ratio
            double mu = Vector3D.length(stepDirection) /
                        Vector3D.length(edgeDirection);
            Vector3D trStepDirection = new Vector3D(trEdgeDirection);
            // vector tickStart -> tickStep, view space
            Vector3D.scaleBy(trStepDirection, mu);        
            // vector tickStep, view space
            Vector3D viewTickStep = Vector3D.plus(viewTickStart, trStepDirection);            
            result[1] = viewTickStep;
            Vector3D lastViewTick = new Vector3D(viewTickStep);
            // create next tickmarks in view space (if any)
            for (int sCnt = 2; sCnt < numTicks[edgeIndex]; sCnt++)
            {   Vector3D nextViewTick = Vector3D.plus(lastViewTick, trStepDirection);
                result[sCnt] = nextViewTick;
                lastViewTick = new Vector3D(nextViewTick);
                
            }
        }
        return result;
    }
    
    /**
     * check if some edge of this Facte3D should be drawn
     * in a non-default color, i.e. its edgeCode is nonzero
     * @return true/false
     */
    private boolean edgesLabeled()
    {   boolean labeled = false;
        for (int i = 0; i < numPoints; i++)
        {   if (edgeCodes[i] != 0)
                return true;
        }    
        return labeled;
    }    
    
    /**
     * paint the edges of the projection of this Facet3D individually
     * only when this Facet3D is non-filled and some edgeCode is nonzero;
     * edgeCodes are as follows: <br>
     * 0 default outline color, 1 line color, 2 plane color, hidden via normal <br>
     * 10 default outline color, 11 line color, 12 plane color, externally hidden <br>
     * 20 default outline color, 21 line color, 22 plane color, highlighted and hidden via normal <br>
     * 30 default outline color, 31 line color, 32 plane color, highlighted and externally hidden <br>
     * 40 default outline color, 41 line color, 42 plane color, externally UNhidden <br>
     * @param g Context2d for drawing 
     * @param p projection of this Facet3D 
     * @param visFromD is the Facet3D visible from the view point 
     */
    public void drawPolygon(Context2d g, Polygon2D p, boolean visFromD)
    {   for (int i = 0; i < p.npoints; i++)
        {   // outline requested
            if (edgeCodes[i] >= 0)
            {   boolean hidden = false;
                boolean highlighted = false;
                boolean grafiek3D = false;
                boolean normalHidden = false;
                // hiding through normal
                if ((edgeCodes[i] >= 0) && (edgeCodes[i] < 10))
                {
                	// use redundancy here
                    // edgeColors[edgeLabels[0]] is the global outlineColor
                    // edge "hidden"
                    if (!visFromD && (hiddenOutlineMode % 2 == 0))
                    {   
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i]]));
                    }        
                    else // not "hidden"
                    {   
                    	g.setStrokeStyle(edgeColors[edgeCodes[i]]);
                    }
                    hidden = !visFromD;    
                }
                // externally hidden
                else if ((edgeCodes[i] >= 10) && (edgeCodes[i] < 20))
                {   if (hiddenOutlineMode % 2 == 0)
            		{   
            			g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 10]));
            		}
                	else // no "hidden" color
                	{   
                		g.setStrokeStyle(edgeColors[edgeCodes[i] % 10]);
                	}
                    hidden = true;    
                }    
                // hightlighted and hiding through normal
                else if ((edgeCodes[i] >= 20) && (edgeCodes[i] < 30))
                {   highlighted = true;
                	if (!visFromD &&
                        
                        (hiddenOutlineMode % 2 == 0)
                        )
                    {    
                        
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 20]));
                    }        
                    else // not "hidden"
                    {   
                    	g.setStrokeStyle(edgeColors[edgeCodes[i] % 20]);
                    }
                    hidden = !visFromD;    
                }
                // highlighted and externally hidden
                else if ((edgeCodes[i] >= 30) && (edgeCodes[i] < 40))
                {   
                	if (hiddenOutlineMode % 2 == 0)
                    {   
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 30]));
                    }
                    else // no "hidden" color
                    {   
                    	g.setStrokeStyle(edgeColors[edgeCodes[i] % 10]);
                    }
                    highlighted = true;
                    hidden = true;
                }    
                // extern unhidden
                else if ((edgeCodes[i] >= 40) && (edgeCodes[i] < 50))
                {   
                	g.setStrokeStyle(edgeColors[edgeCodes[i] % 40]);
                }    

                else if (edgeCodes[i] == 50)
                {
                	grafiek3D = true;
                	hidden = false;
                	normalHidden = !visFromD;
                	
                }
                else if (edgeCodes[i] == 51)
                {
                	grafiek3D = true;
                	hidden = true;
                }
                
                else if (edgeCodes[i] == 52)
                {
                	grafiek3D = true;
                	hidden = false;
                	if (!filled)
                		normalHidden = visFromD;
                	else
                		normalHidden = !visFromD;
                }
                
                // drawing
                // Doorzien
                if (!grafiek3D)
                {	
                	if (!hidden || (hiddenOutlineMode == 0))    
                	{	g.beginPath();
                		g.moveTo(p.xpoints[i], p.ypoints[i]);
                		g.lineTo(p.xpoints[(i+1) % p.npoints],p.ypoints[(i+1) % p.npoints]);
                		g.stroke();

                	}
                	else if (hidden && (hiddenOutlineMode != 0))    // dashed
                	{	drawDashedLine(g, p.xpoints[i], p.ypoints[i],
                                       p.xpoints[(i+1) % p.npoints], 
                                       p.ypoints[(i+1) % p.npoints]);
                	}
                }
                else // for grafiek3D
                {	if (!normalHidden)
                	{	
                		g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                	}
                	else
                	{	
                		g.setStrokeStyle(outlineColor);
                	}
                	if (!hidden)    
                	{	
                		g.beginPath();
                		g.moveTo(p.xpoints[i], p.ypoints[i]);
                		g.lineTo(p.xpoints[(i+1) % p.npoints], p.ypoints[(i+1) % p.npoints]);
                		g.stroke();
                	
                	}
                	
                	else if (filled)
                	{	g.setStrokeStyle(fillColor);
                		
                		g.beginPath();
                		g.moveTo(p.xpoints[i], p.ypoints[i]);
                		g.lineTo(p.xpoints[(i+1) % p.npoints], p.ypoints[(i+1) % p.npoints]);
                		g.stroke();
                		
                	}
                }
            }                          
        } // for
    }    
    
    /**
     * draw a dashed line between the plane points (x1,y1) and (x2,y2)
     * @param g Context2d for drawing
     * @param x1 x-coordinate first point
     * @param y1 y-coordinate first point
     * @param x2 x-coordinate second point
     * @param y2 y-coordinate second point
     */
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

    /**
     * find the distance between the plane points (x1,y1) and (x2,y2)
     * @param x1 x-coordinate first point
     * @param y1 y-coordinate first point
     * @param x2 x-coordinate second point
     * @param y2 y-coordinate second point
     * @return distance
     */
    private double pixDisD(double x1, double y1, double x2, double y2)
    {   return Math.sqrt((x1 - x2) * (x1 - x2) +
                         (y1 - y2) * (y1 - y2));
        
    }    
    

    /**
     * given an outline color for edges or segments,
     * return the outline color when the edge or segment
     * is hidden (i.e. behind some other facet) 
     * @param c outline color
     * @return outline color if hidden
     */
    public static CssColor getHiddenOutlineColor(CssColor c)    
    {   CssColor result = c;
        if (c.toString().equals(black.toString()))
        {  	return lightGray;
        }
        if (c.toString().equals(blue.toString()))
            return mediumBlue;
        if (c.equals(red))
        {   return lightRed;
        }    
        if (c.toString().equals(brownRed.toString()))
        {   return lightRed;
        }	
        return result;
    }    
    
    /**
     * find a shadowed version of the color of Facet3D f using
     * the unitNormal or the reverse unitNormal of f
     * @param f the Facet3D
     * @param reverseNormal true: use the reverse unitNormal 
     * @return shadowed color
     */
    public static CssColor shadowColor(Facet3D f, boolean reverseNormal)
    {   // on x^2+y^2+z^2=1 with z>=0 -n.x-n.y+n.z has maximum sqrt(3) and
        // minimum -sqrt(2) for x^2=y^2=z^2=1/3 resp x=y=sqrt(2)/2, z=0
        // this should be >=0 and <= 1
        // see method shadowGrayColor
        
        if (Vector3D.equals(f.unitNormal, new Vector3D()))
            return CssColor.make(0,0,0);
            
        double grayFactor;
        if (reverseNormal)
            grayFactor = (f.unitNormal.x + f.unitNormal.y - f.unitNormal.z +
                          Math.sqrt(3)) /
                         (Math.sqrt(2) + Math.sqrt(3));
        else //!!!
            grayFactor = (- f.unitNormal.x - f.unitNormal.y + f.unitNormal.z +
                          Math.sqrt(3)) /
                         (Math.sqrt(2) + Math.sqrt(3));
        if (grayFactor < 0)
            grayFactor = 0;
        if (grayFactor > 1)
            grayFactor = 1;
        // use this for contrast
        grayFactor = Math.pow(grayFactor, 5e-1d);

        String fString = f.color.toString().substring(4, f.color.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(fString,",");

		int fBlue =  Integer.parseInt(kleurenStr[2]);
		int fGreen = Integer.parseInt(kleurenStr[1]);
		int fRed =   Integer.parseInt(kleurenStr[0]);
        
        int red = 50 + (int)(fRed * grayFactor * (8e-1d) );
        int green = 50 + (int)(fGreen * grayFactor * (8e-1d) );
        int blue = 50 + (int)(fBlue * grayFactor * (8e-1d) );
		
        // redundant?
        if (red > 255)
        	red = 255;
        if (green > 255)
        	green = 255;
        if (blue > 255)
        	blue = 255;
	     return CssColor.make(red, green, blue);
    }

    /**
     * find a shadowed grey color for the inside of Facet3D f
     * using the reverse unitNormal of f
     * @param f the Facet3D 
     * @return the sahdowed grey color
     */
    public static CssColor shadowGrayColor(Facet3D f)
    {   // on x^2+y^2+z^2=1 with z<0 n.x+n.y-n.z has maximum sqrt(2) and
        // minimum -sqrt(3) for x=y=sqrt(2)/2, z=0 resp x^2=y^2=z^2=1/3
        // this should be >=0 and <= 1
        
        if (Vector3D.equals(f.unitNormal, new Vector3D()))
            return black;
            
        double grayFactor = (f.unitNormal.x + f.unitNormal.y - f.unitNormal.z + 
                             Math.sqrt(3)) /
                            (Math.sqrt(2) + Math.sqrt(3));
        if (grayFactor < 0)
            grayFactor = 0;
        if (grayFactor > 1)
            grayFactor = 1;
        
        int gray = (int)(255 * grayFactor * (8e-1d));
        // redundant?
        if (gray > 255)
        	gray = 255;
        if (gray < 0)
        	gray = 0;
        
        return CssColor.make(gray, gray, gray);
    }

    /**
     * given Facet3D orig and copy (with the same number
     * of vertices) copy all attributes from orig to copy;
     * if exact == true, copy also the vertex and edge codes,
     * the vertex labels and the tick marks  
     * @param orig original Facet3D
     * @param copy copy Facet3D 
     * @param exact true/false
     */
    public static void copyAttributes(Facet3D orig, Facet3D copy, boolean exact)
    {   copy.outlined = orig.outlined;
        copy.filled = orig.filled;
        copy.empty = orig.empty;
        copy.visible = orig.visible;
        copy.color = orig.color;
        copy.outlineColor = orig.outlineColor;
        copy.hiddenOutlineMode = orig.hiddenOutlineMode;
        copy.edgeColors = orig.edgeColors;
        
        if (exact)
        {   for (int m = 0; m < orig.numPoints; m++)            
            {   copy.vertexCodes[m] = orig.vertexCodes[m];
                copy.edgeCodes[m] = orig.edgeCodes[m];
                if (orig.vertexLabels[m] != null)
                    copy.vertexLabels[m] = new String(orig.vertexLabels[m]);
                if (orig.tickStart[m] != null)
                    copy.tickStart[m] = new Vector3D(orig.tickStart[m]);
                if (orig.tickStep[m] != null)
                    copy.tickStep[m] = new Vector3D(orig.tickStep[m]);
                copy.numTicks[m] = orig.numTicks[m];
                copy.drawTicks[m] = orig.drawTicks[m];                                    
            }    
            if ((orig.numPoints == 2) && orig.normal.equals(new Vector3D()))
                copy.normal = new Vector3D();
        }    
    }
    /**
     * check if Facet3D f contains vertex v, this cannot be done by reference, 
     * since v might belong to another object!
     * @param f the Facet3D
     * @param v the vertex v
     * @return -1 (no) or the index of vertex v in Facet3D
     */
    public static int containsVertex(Facet3D f, Vector3D v)
    {   int index = -1;
        for (int i = 0; i < f.numPoints; i++)
        {   if (Vector3D.equals(v, f.points[i]))
               return i;
        }
        return index;
    }
    /**
     * check if Facet3D f contains the directed edge v1v2, necessary to find adjacent facets
     * @param f the Facet3D
     * @param v1 start of directed edge v1v2
     * @param v2 end of directed edge v1v2
     * @return return -1 (no) or the index of vertex v1 in Facet3D
     */
    public static int containsEdge(Facet3D f, Vector3D v1, Vector3D v2)
    {   int index1 = containsVertex(f, v1);
        if (index1 < 0)
            return -1;
        int index2 = containsVertex(f, v2);
        if (index2 < 0)
            return -1;
        int nextTo1 = (index1 + 1) % f.numPoints;
        if (index2 == nextTo1)
            return index1;
        else
            return -1;
    }
    /**
     * check if an edge of Facet3D f contains the directed segment
     * that is we have edgeStart,v1,v2,edgeEnd (in that order)
     * assume v1 and v2 different 
     * @param f the Facet3D
     * @param v1 first point of directed segment
     * @param v2 second point of directed segment
     * @return -1 (no) or the index of the edge in Facet3D f
     */
    public static int edgeContainsDirSegment(Facet3D f, Vector3D v1, Vector3D v2)
    {   int result = -1;
        for (int i = 0; i < f.numPoints; i++)
        {   Vector3D edgeStart = f.points[i];
            Vector3D edgeEnd = f.points[(i + 1) % f.numPoints];
            Line3D edgeLine = new Line3D(edgeStart, edgeEnd);
            boolean v1On = edgeLine.segmentContains(v1);
            boolean v2On = edgeLine.segmentContains(v2);
            if (v1On && v2On)
            {   if (Vector3D.distance(v1, edgeStart) <
                    Vector3D.distance(v2, edgeStart))
                    return i;    
                // else keep looking
            }
            // else keep looking
        }
        return result;
    }
    
    /**
     * check if an edge of Facet3D f contains the segment
     * v1,v2 as a subset, that is we have edgeStart,v1,v2,edgeEnd
     * or edgeStart,v2,v1,edgeEnd; assume v1 and v2 different 
     * @param f the Facet3D
     * @param v1 first point of segment
     * @param v2 second point of segment
     * @return -1 (no) or the index of the edge in Facet3D f
     */
    public static int edgeContainsSegment(Facet3D f, Vector3D v1, Vector3D v2)
    {   int result = -1;
        for (int i = 0; i < f.numPoints; i++)
        {   Vector3D edgeStart = f.points[i];
            Vector3D edgeEnd = f.points[(i + 1) % f.numPoints];
            Line3D edgeLine = new Line3D(edgeStart, edgeEnd);
            boolean v1On = edgeLine.segmentContains(v1);
            boolean v2On = edgeLine.segmentContains(v2);
            if (v1On && v2On)
                return i;    
            // else keep looking
        }
        return result;        
    }

    /**
     * check if an edge of Facet3D f contains the poiny v
     * @param f the Facet3D
     * @param v point to be checked
     * @return true/false
     */
    public static int edgeContainsPoint(Facet3D f, Vector3D v)
    {   int result = -1;
        for (int i = 0; i < f.numPoints; i++)
        {   Vector3D edgeStart = f.points[i];
            Vector3D edgeEnd = f.points[(i + 1) % f.numPoints];
            Line3D edgeLine = new Line3D(edgeStart, edgeEnd);
            boolean vOn = edgeLine.segmentContains(v);
            if (vOn)
                return i;    
            // else keep looking
        }
        return result;        
    }
    
    /**
     * check if the point v is within Facet3D f, that is,
     * v should be in the plane through f and inside f;
     * if f is a segment, v should be on the segment
     * @param f the Facet3D
     * @param v point to be checked
     * @return true/false
     */
    public static boolean containsPoint(Facet3D f, Vector3D v)
    {   if (f.numPoints == 2)
        {   Line3D line = new Line3D(f.points[0], f.points[1]);
            return line.segmentContains(v);
        }
        if (f.numPoints > 2)
        {   Plane3D tPlane = new Plane3D(
                f.normal.x, f.normal.y, f.normal.z,
                Vector3D.dotProduct(f.normal,
                                    f.points[0]));  
// avoid collinearity        
                //f.points[0], f.points[1], f.points[2]);
            if (!tPlane.contains(v))
                return false;
            // now v is in tPlane    
            // find barycenter of facet    
            double xSum = 0;
            double ySum = 0;
            double zSum = 0;
            for (int i = 0; i < f.numPoints; i++)
            {   xSum += f.points[i].x;
                ySum += f.points[i].y;
                zSum += f.points[i].z;
            }
            Vector3D bc = new Vector3D(xSum / f.numPoints,
                            ySum / f.numPoints, zSum / f.numPoints);
            boolean result = true;                
            for (int j = 0; j < f.numPoints; j++)                
            {   Vector3D edgeStart = f.points[j];
                Vector3D edgeEnd = f.points[(j + 1) % f.numPoints];
                Line3D line = new Line3D(edgeStart, edgeEnd);
                // check if v is on this edge
                if (line.segmentContains(v))
                    return true;
                // find plane orthogonal to facet and through line
                // passes through line.support, line.support + line.direction
                // and line.support + facet.normal
                Plane3D oPlane = new Plane3D(line.support,
                    Vector3D.plus(line.support, line.direction),
                    Vector3D.plus(line.support, f.normal));
                int ppbc = oPlane.planePosition(bc);
                int ppv = oPlane.planePosition(v);
                // v is inside if for all edges it is on the same side
                // as the barycenter, note that at this point v is not
                // on an edge, thus ppv != 0
                result = result && (ppbc == ppv);
            }    
            return result;
        }    
        return false;
    }
    /**
     * check if Facet3d f1 equals Facet3D f2 in this sense:
     * both facets contain the same set of vertices AND the
     * directed vertex arrays are equal up to cyclic permutation, that is 
     * the facets have the same orientation
     * @param f1 first Facet3D
     * @param f2 second Facet3D
     * @return return -1 (not equal) or the index of the vertex in Facet3D f2 that corresponds to
     * points[0] of Facet3D f1
     */
    public static int isEqualTo(Facet3D f1, Facet3D f2)
    {   if (f1.numPoints != f2.numPoints)
            return -1;
        // start with the first vertex of f1 and do everything in one loop
        // find this index in f2
        int f2Index = Facet3D.containsVertex(f2, f1.points[0]);
        if (f2Index < 0)
            return -1;
        for (int i = 1; i < f1.numPoints; i++)
        {   Vector3D f2Next = f2.points[(f2Index + i) % f2.numPoints];
            if (!Vector3D.equals(f1.points[i], f2Next))
                return -1;
        }
        // if we get up to here the desired equality is reached
        return f2Index;
    }
    /**
     * check if Facet3d f1 is (apart from a cyclic permutation of its vertices) the reverse of Facet3D f2
     * @param f1 first Facet3D
     * @param f2 second Facet3D
     * @return return -1 (not each others reverse) or the index of the vertex in Facet3D f2 that corresponds to
     * points[0] of Facet3D f1
     */
    public static int isReverseTo(Facet3D f1, Facet3D f2)
    {   if (f1.numPoints != f2.numPoints)
            return -1;
        // start with the first vertex of f1 and do everything in one loop
        // index of first index of f1 in f2
        int f2Index = Facet3D.containsVertex(f2, f1.points[0]);
        if (f2Index < 0)
            return -1;
        for (int i = 1; i < f1.numPoints; i++)
        {   int temp = f2Index - i;
            if (temp < 0)
                temp += f2.numPoints;
            Vector3D f2Next = f2.points[temp];
            if (!Vector3D.equals(f1.points[i], f2Next))
                return -1;
        }
        // if we get up to here the desired equality is reached
        return f2Index;
    }
    /**
     * reverse the orientation of Facet3D f, that is reverse the order 
     * of the indices of the vertices in the vertex array of the Object3D to which f belongs;
     * the normal vector of f is not changed!
     * @param f the Facet3D to be reversed
     */
    public static void reverse(Facet3D f)
    {   // new index array
        int[] indices = new int[f.numPoints];
        for (int i = 0; i < f.numPoints / 2; i++)
        {   indices[i] = f.indices[f.numPoints - 1 - i];
            indices[f.numPoints - 1 - i] = f.indices[i];
        }
        if (f.numPoints % 2 == 1)
            indices[f.numPoints / 2 + 1] = f.indices[f.numPoints / 2 + 1];
        f.indices = indices;    
    }
    /**
     * find the surface area of Facet3D f 
     * @param f the Facet3D
     * @return the surface area of Facet3D f
     */
    public static double getSurfaceArea(Facet3D f)
    {   double area = 0;
        // find barycenter
        double bx = 0, by = 0, bz = 0;
        for (int pCnt = 0; pCnt < f.numPoints; pCnt++)
        {   bx += f.points[pCnt].x;
            by += f.points[pCnt].y;
            bz += f.points[pCnt].z;
        }    
        Vector3D bary = new Vector3D(bx / f.numPoints, 
                                     by / f.numPoints, 
                                     bz / f.numPoints);
        for (int eCnt = 0; eCnt < f.numPoints; eCnt++)
        {   // get edge eCnt
            Vector3D eStart = f.points[eCnt];
            Vector3D eEnd = f.points[(eCnt + 1) % f.numPoints];
            Vector3D eDir = Vector3D.minus(eEnd, eStart);
            // just in case
            if (!eDir.equals(new Vector3D()))
            {   // teken een plaatje!!
                Vector3D barProj = Vector3D.projectOn(bary, eDir);
                Vector3D startProj = Vector3D.projectOn(eStart, eDir);
                Vector3D projDif = Vector3D.minus(barProj, startProj);
                Vector3D proj = Vector3D.plus(projDif, eStart);
                double base = Vector3D.distance(eStart, eEnd);
                double height = Vector3D.distance(bary, proj);
                area += (base * height / 2);
            }                        
        }
        return area;
    }
} // class Facet3D
