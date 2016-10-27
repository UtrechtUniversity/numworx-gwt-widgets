package fi.grafiek3dgwt.client;


import java.awt.*;
//import java.io.Serializable;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;


// a facet of a 3D figure, i.e. a polygon with its points located in some
// plane of 3-space
// the facet contains only references to instances of vertices in the
// object vertices array to which the facet belongs. Thus
// at construction time the vertices array of the object to which this facet
// belongs must be have been created and initialized with the objects vertices
// the transformed vertices of the facet cannot be referenced in the constructor
// since the objects array of transformed vertices at this stage only contains
// null pointers so that a method is needed to get the correct references
// when they are available
// the objects arrays of (transformed) vertices should NOT be changed
// otherwise the indices do not make sense anymore
public class Facet3D //implements Serializable
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
    
	
    //public final Color darkGreen = new Color(41, 156, 57); 
    //public final Color mediumGreen = new Color(173, 222, 99);
    //public static final Color brownRed = new Color(214, 0, 0);
    //public static final Color lightRed = new Color(255, 156, 74);
    //public static final Color mediumBlue = new Color(99, 198, 222);    
	
	// public attributes
    // the number of 3D points in the facet
    public int numPoints;
    // indices of the points in the real/integer arrays of the object
    // to which this facet belongs
    public int[] indices;
    // the 3D real points of the facet, references only, world space
    public Vector3D[] points;
    // the transformed real points of the facet, references only, view space
    public Vector3D[] trPoints;
    // drawing individual vertices
    public int[] vertexCodes;        
    // drawing individual edges
    public int[] edgeCodes;   
    // drawing labels for vertices
    public String[] vertexLabels;
    
    //public static Font vertexFont = new Font("SansSerif", Font.ITALIC, 12);
        
// hier vertexLabels, references to Strings
// regel:
// maak de vertexlabels van een object in initObject,
// dus na initObject de vertexlabels "zetten"
// initialiseer daarna de vertex labels van de facets
// ze staan op dezelfde plaatsen als de vertices
// vgl. updatePoints
// voor het tekenen heb je de vertexLabels HIER nodig
    
    // normal vector of the polygon and its unitary transformed
    public Vector3D normal, unitNormal;
    // reference to all vertices array
//    public Vector3D[] allPoints;
    // true if edges should be drawn
    public boolean outlined = true;
    // true if filled
    // note that this facet in this situation can still be clicked
    public boolean filled = true;
    // true if neither outline nor filling
    // note that this facet in this situation can still be clicked
    public boolean empty = false;
    // to be drawn or not, if not drawn not clickable
    public boolean visible = true;
    // the color of the facet
    public CssColor color;
    // the color of the outline
    public CssColor outlineColor = CssColor.make(0,0,0);
    // the actual fill color used (thus adapted for shadow etc)
    public CssColor fillColor;

// changing via rebuild!    
    // how to draw "hidden" outlines in non-filled mode
    // voor later 0 = lighter, 1 = dashed 2 = dashed and lighter    
    public int hiddenOutlineMode = 0; //Grafiek3DComponent.hiddenOutlineMode;
    
    // colors for edges, reference sufficient
    // these are the non-hidden colors
    public CssColor lineColor = blue;
    public CssColor planeOutlineColor = brownRed;    
    public CssColor pointColor = darkGreen;
    
    //public Color[] edgeColors = Grafiek3DComponent.edgeColors;
    public CssColor[] edgeColors =
		{outlineColor, lineColor, planeOutlineColor, pointColor};

    // average height above x-y-plane after transforming to
    // view space, needed for Painter's Algo etc.
    public double zValue;
    // barycenter in view space
    public Vector3D barycenter;
    
    // voor Doorzien
    Facet3D isReplacementOf;
    
    // dash length in pixels
    public static int DASH = 4;
    
    // "highlighting points"
    public static int BIGPOINT = 7;
    public static int SMALLPOINT = 4;
    boolean thickenVertices = false;

    Vector3D[] tickStart;
    Vector3D[] tickStep;
    int[] numTicks;
    boolean[] drawTicks; 
    boolean ticksVisible = false;
    public CssColor tickColor = darkGreen;
    public CssColor hiddenTickColor = mediumGreen;
    
// zorg in edgeClicked dat je ook alleen de ticks 
// mag aanklikken!!!!!!
    
    boolean letters = false;
    public CssColor letterColor = CssColor.make(0,0,0);
    
    boolean isOnAxis = false;
    
    // constructor from vertices and indices
    public Facet3D(Vector3D[] pts, int[] inds, CssColor c)
    {   // set reference
//        allPoints = pts;
        // init indices
        indices = inds; // new int[inds.length];
        // get the number of points in the facet as the length
        // of indices
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
        // coordinate of first tick point
        // initialized as null        
        tickStart = new Vector3D[numPoints];
        // coordinate of second tick point (if any)
        // initialized as null        
        tickStep = new Vector3D[numPoints];
        // initialized as zero
        numTicks = new int[numPoints];
        // initialized as false
        drawTicks = new boolean[numPoints];
        
        for (int i = 0; i < numPoints; i++)
        {   // allPoints[indices[i]] is the Vector3D we need
            points[i] = pts[indices[i]];
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
    
    // called if vertices have been changed
    public void updatePoints(Vector3D[] pts)
    {   for (int i = 0; i < numPoints; i++)
            points[i] = pts[indices[i]];
        setNormal();    
    }
    
    // called if transformed vertices have been changed
    public void updateTrPoints(Vector3D[] trPts)
    {   for (int i = 0; i < numPoints; i++)
            trPoints[i] = trPts[indices[i]];
    }
    // assume only consecutive vertices can coincide
    public void checkDoublePoints()
    {   for (int i = 0; i < numPoints; i++)
        {   if (Vector3D.equals(points[i], points[(i + 1) % numPoints]))
                deletePoint(i);
        }
    }
    private void deletePoint(int index)
    {   // shift for index = 0 to index = numPoints - 2
        for (int i = index + 1; i < numPoints - 1; i++)
        {   indices[i] = indices[i + 1];
            points[i] = points[i + 1];
        }
        numPoints--;
    }
    // find untransformed normal vector
    public void setNormal()
    {   // point
        if (numPoints == 1)
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
            normal = Vector3D.crossProduct(v1, v2); // which could be 0-vector
        }
    }
    
    public void reverseNormal()
    {
    	normal = new Vector3D(-normal.x, -normal.y, -normal.z);
    }
    // average z-coordinate of vertices of trPoints!
    // used after transforming
    public void calculateZValue(Vector3D origin, double distance)
    {   
/*        
        double zSum = 0;
        for (int i = 0; i < numPoints; i++)
            zSum += trPoints[i].z;
        zValue= zSum / numPoints;
*/        
        
        double bX = 0;
        double bY = 0;        
        double bZ = 0;                
        for (int i = 0; i < numPoints; i++)
        {   bX += trPoints[i].x;
            bY += trPoints[i].y;
            bZ += trPoints[i].z;
        }
        Vector3D bC = new Vector3D(
            bX / numPoints, bY / numPoints, bZ / numPoints);
        Vector3D eye = new Vector3D(origin.x, origin.y, distance);    
        zValue = distance - Vector3D.distance(bC, eye);    
        
        
        
    }
    
    
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
    
    // project the transformed facet on the plane z = 0 from
    // distance = d > 0 in the positive direction of the z-axis,
    // thus from the view point (o.x, o.y, d)
    // projecting on another plane can be done analogously
    public Polygon project(double d, Vector3D o)
    {   //if (!visible)
        //    return null;
        // projected polygon
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

    // exact REAL projection
    public Polygon2D project2D(double d, Vector3D o)
    {   //if (!visible)
        //    return null;
        // projected polygon
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
    
    
    public void paintFacet3D(Context2d g, boolean shadow, boolean inside, 
                             double dis, Matrix3D mat, Object3D ob)
    {
        
// NOTE ob != null requests exact drawing through edge coverings       
        Polygon2D p = project2D(dis, mat.origin);

// the transformed facet is visible from (origin.x, origin.y, d) if 
// (origin.x, origin.y, d) is at the side of the transformed
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
        // inside = false geeft zelfde kleur met goede schaduw
        // via  -unitNormal
        if (inside && shadow)
        {   if (visFromD)
    		{   //g.setColor(Facet3D.shadowColor(this, false));
    			g.setFillStyle(Facet3D.shadowColor(this, false));
    			fillColor = Facet3D.shadowColor(this, false);
    		}
        	else
        	{   //g.setColor(Facet3D.shadowGrayColor(this));
        		g.setFillStyle(Facet3D.shadowGrayColor(this));
        		fillColor = Facet3D.shadowGrayColor(this);
        	}
        }
        else if (!inside && shadow)
        {   if (visFromD)
        	{    //g.setColor(Facet3D.shadowColor(this, false));
    			g.setFillStyle(Facet3D.shadowColor(this, false));
    			fillColor = Facet3D.shadowColor(this, false);
        	}
        	else
        	{   //g.setColor(Facet3D.shadowColor(this, true));
        		g.setFillStyle(Facet3D.shadowColor(this, true));
        		fillColor = Facet3D.shadowColor(this, true);
        	}
        }
        else if (inside && !shadow)
        {   if (visFromD)
    		{   //g.setColor(color);
    			g.setFillStyle(color);
    			fillColor = color;
    		}
        	else
        	{   //g.setColor(Color.lightGray);
        		g.setFillStyle(lightGray);
        		fillColor = lightGray;
        	}
        }
        else // none
        {   //g.setColor(color);
        	g.setFillStyle(color);
        	fillColor = color;
        }

        if (filled && (numPoints >= 3))
        {   //g.fillPolygon(p);
        
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
// voor filled facets (dus geen segmenten) 
// is nooit exacte tekening gewenst
        if (outlined)
        {    
             // facet filled, no edge colors overidden
             if (filled && !edgesLabeled())
             {   // for lines change outlineColor externally
            	 if (visFromD)             
                 {   //g.setColor(outlineColor);
                	 g.setStrokeStyle(outlineColor);
                 }
                 else
                 {   //g.setColor(getHiddenOutlineColor(outlineColor));
                	 g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                 }                    
             }
             // facet transparent, no edge colors overridden 
             else if (!filled && !edgesLabeled())
             {   
// aparte constructor voor 2-dim facets

// wanneer is een lijn nu hidden?                
// "inside line" kleur extern "hidden" zetten
// hidden line normaal 
// anders normalvector = "directed segment"?
// for lines change (hidden)outlineColor externally
            	 
            	 if (numPoints < 3)
            	 {	//g.setColor(outlineColor);
            	 	g.setStrokeStyle(outlineColor);
            	 }
            	 else if (!visFromD && (hiddenOutlineMode % 2 == 0))
                 {   //g.setColor(getHiddenOutlineColor(outlineColor));
                	 g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                 }
                 else    
                 {   //g.setColor(outlineColor);
                	 g.setStrokeStyle(outlineColor);
                 }
             }    
// hier kijken of exacte tekening gewenst             
             // edge colors overridden
             if (edgesLabeled() || (hiddenOutlineMode != 0))
             {    drawPolygon(g, p, visFromD);
             
             }
             else  
             {   if (numPoints < 3) 
             	 {	//g.drawLine(p.xpoints[0], p.ypoints[0], p.xpoints[1], p.ypoints[1]);
            	 
            	 	if (numPoints > 1)
            	 	{	
            	 		g.beginPath();
            	 		g.moveTo(p.xpoints[0], p.ypoints[0]);
            	 		g.lineTo(p.xpoints[1], p.ypoints[1]);
            	 		g.stroke();
            	 	}	
             	 }
            	 else
            	 {	 //g.drawPolygon(p);

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
        				{	//g.setColor(edgeColors[vertexCodes[cnt]]);
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt]]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt]]);
        				
        				}
                        if (!visFromD && (hiddenOutlineMode % 2 == 0)
                        	)
                        {   //g.setColor(getHiddenOutlineColor(edgeColors[vertexCodes[cnt]]));
                        	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt]]));
                        	g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt]]));
                        }        
                        else // not "hidden"
                        {   //g.setColor(edgeColors[vertexCodes[cnt]]);
                        	g.setStrokeStyle(edgeColors[vertexCodes[cnt]]);
                        	g.setFillStyle(edgeColors[vertexCodes[cnt]]);
                        }
        				//hidden = !visFromD;    
        			}
        			// externally hidden
        			else if ((vertexCodes[cnt] >= 10) && (vertexCodes[cnt] < 20))
        			{   if (hiddenOutlineMode % 2 == 0)
                	 	{    //g.setColor(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 10]));
              	 	  		g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 10]));
              	 	  		g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 10]));
                	 	}
        				else // no "hidden" color
        				{    //g.setColor(edgeColors[vertexCodes[cnt] % 10]);
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 10]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt] % 10]);
        				}
                     	//hidden = true;    
        			}    
        			// hightlighted and hiding through normal
        			else if ((vertexCodes[cnt] >= 20) && (vertexCodes[cnt] < 30))
        			{   highlighted = true;
        				if (!visFromD && (hiddenOutlineMode % 2 == 0))
        				{   //g.setColor(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 20]));
        					g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 20]));
        					g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 20]));
        				}        
        				else // not "hidden"
        				{   //g.setColor(edgeColors[vertexCodes[cnt] % 20]);
        					g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 20]);
        					g.setFillStyle(edgeColors[vertexCodes[cnt] % 20]);
        				}
        				//hidden = !visFromD;    
        			}
        			// highlighted and externally hidden
        			else if ((vertexCodes[cnt] >= 30) && (vertexCodes[cnt] < 40))
        			{   
        				if (hiddenOutlineMode % 2 == 0)
                        {    //g.setColor(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 30]));
                       	 	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 30]));
                       	 	g.setFillStyle(getHiddenOutlineColor(edgeColors[vertexCodes[cnt] % 30]));
                        }
                        else // no "hidden" color
                        {   //g.setColor(edgeColors[vertexCodes[cnt] % 10]);
                       	 	g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 30]);
                       	 	g.setFillStyle(edgeColors[vertexCodes[cnt] % 30]);
                        }
        				highlighted = true;
                     //hidden = true;
        			}    
        			// extern unhidden
        			else if ((vertexCodes[cnt] >= 40) && (vertexCodes[cnt] < 50))
        			{   //g.setColor(edgeColors[vertexCodes[cnt] % 40]);
                   	 	g.setStrokeStyle(edgeColors[vertexCodes[cnt] % 40]);
                   	 	g.setFillStyle(edgeColors[vertexCodes[cnt] % 40]);
                    }    
        			if (highlighted)
        			{	
        				//g.fillOval(p.xpoints[cnt] - BIGPOINT / 2,
        				//		p.ypoints[cnt] - BIGPOINT / 2, 
                        //        	BIGPOINT, BIGPOINT);
        				
        				g.beginPath();
        				g.fillRect(p.xpoints[cnt] - BIGPOINT / 2,
                                p.ypoints[cnt] - BIGPOINT / 2, 
                                BIGPOINT, BIGPOINT);
        				
        			}	
        			else
        			{
        				//g.fillOval(p.xpoints[cnt] - SMALLPOINT / 2,
        				//			p.ypoints[cnt] - SMALLPOINT / 2, 
        				//			SMALLPOINT, SMALLPOINT);
        				//g.drawOval(p.xpoints[cnt] - SMALLPOINT / 2,
        				//			p.ypoints[cnt] - SMALLPOINT / 2, 
                        //        	SMALLPOINT - 1, SMALLPOINT - 1);
        				
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
    		{   //g.setColor(DrawConstants.tickColor);
    			g.setStrokeStyle(tickColor);
    		}
        	else	    
        	{   //g.setColor(DrawConstants.hiddenTickColor);
        		g.setStrokeStyle(hiddenTickColor);
        	}
            for (int eCnt = 0; eCnt < numPoints; eCnt++)
            {   if (drawTicks[eCnt] && (numTicks[eCnt] > 0))
                {   Vector3D[] trTicks = findTransformedTicks(eCnt);
                    Point[] projTicks = projectPoints(trTicks, dis, mat.origin);
                    for (int pCnt = 0; pCnt < projTicks.length; pCnt++)
                    {
                         //g.fillOval(projTicks[pCnt].x - SMALLPOINT / 2,
                         //           projTicks[pCnt].y - SMALLPOINT / 2, 
                         //           SMALLPOINT, SMALLPOINT);
                         //g.drawOval(projTicks[pCnt].x - SMALLPOINT / 2,
                         //           projTicks[pCnt].y - SMALLPOINT / 2, 
                         //           SMALLPOINT - 1, SMALLPOINT - 1);
                    
                         g.beginPath();
                         g.arc(projTicks[pCnt].x, projTicks[pCnt].y, SMALLPOINT / 2, 0, 2 * Math.PI);
                         
                    }
                }    
            }
        }
       
        // drawing letters        
        //if (Grafiek3DComponent.letters)
        if (letters)
        {   
        	//g.setFont(Grafiek3DComponent.assenFont);
            //g.setColor(letterColor);
            //FontMetrics vertexFM = g.getFontMetrics(g.getFont());
        	
        	g.setStrokeStyle(black);
        	g.setFillStyle(black);
        	            
            String fontString = "10px bold sans-serif";
    		g.setFont(fontString);
        	    		
    		TextMetrics tm = g.measureText("XX");
    		double letterBreedte = tm.getWidth();
//System.out.println(g.getFont().toString());            
            for (int vCnt = 0; vCnt < p.npoints; vCnt++)
            {
                
                if ((vertexLabels[vCnt] != null) 
                    //&& 
                    //(!vertexLabels[vCnt].equals("XX"))
                    //&&
                    //(!vertexLabels[vCnt].equals(""))
                    )
                {
                	if (isOnAxis && !vertexLabels[vCnt].equals(""))
                	{	//g.fillOval(p.xpoints[vCnt] - SMALLPOINT / 2 + 1,
    					//		   p.ypoints[vCnt] - SMALLPOINT / 2 + 1, 
    					//		   SMALLPOINT - 1, SMALLPOINT - 1);
                	
                		g.beginPath();
                		g.arc(p.xpoints[vCnt], p.ypoints[vCnt], SMALLPOINT / 2, 0, 2 * Math.PI);
                		//g.stroke();
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
//System.out.println("floorLabel = " + floorLabel);                		
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
                		{	//bx -= (vertexFM.stringWidth(vertexLabels[vCnt]) / 2);
                			bx -= labelBreedte / 2;	
                    		//by += vertexFM.getAscent();
                			by += labelHoogte;
                		}
                		else if (!floorLabel && (shiftX < 0) && (shiftY >= 0))
                		{	bx += 3;
                    		//by += vertexFM.getAscent();
                			by += labelHoogte;
                		}
                		else if (!floorLabel && (shiftX > 0) && (shiftY < 0))
                		{	//bx -= (vertexFM.stringWidth(vertexLabels[vCnt]) / 2);
                			bx -= labelBreedte / 2;
                    		//by += vertexFM.getAscent() + 3;
                			by += labelHoogte + 3;
                		}
                		else if (!floorLabel && (shiftX < 0) && (shiftY < 0))
                		{	//bx -= (vertexFM.stringWidth(vertexLabels[vCnt]) / 2);
                			bx -= labelBreedte / 2;
                    		//by += vertexFM.getAscent() + 3;
                			by += labelHoogte + 3;
                    	}
                    	else if (floorLabel && (shiftX > 0) && (shiftY >= 0))
                    	{	//bx -= (vertexFM.stringWidth(vertexLabels[vCnt]) / 2);
                    		bx -= labelBreedte / 2;
                    		by -= 3;//vertexFM.getAscent();
                    	}
                    	else if (floorLabel && (shiftX < 0) && (shiftY >= 0))
                    	{	//bx = bx - (vertexFM.stringWidth(vertexLabels[vCnt]) / 2) + 3;
                    		bx = bx - labelBreedte / 2 + 3;
                    		by -= 3;
                    	}
                    	else if (floorLabel && (shiftX > 0) && (shiftY < 0))
                    	{	//bx -= (vertexFM.stringWidth(vertexLabels[vCnt]) / 2);
                    		bx -= labelBreedte / 2;
                    		//by += vertexFM.getAscent() + 3;
                    		by += labelHoogte + 3;
                    	}
                    	else if (floorLabel && (shiftX < 0) && (shiftY < 0))
                    	{	//bx = bx - (vertexFM.stringWidth(vertexLabels[vCnt]) / 2) + 3;
                    		bx = bx - labelBreedte / 2 + 3;	
                    		//by += vertexFM.getAscent() + 3;
                    		by += labelHoogte + 3;
                    	}


                		//g.drawLine(ox, oy, bx, by);
//System.out.println("sh = " + floorLabel + " " + shiftX + " " + shiftY);

						//g.drawString(vertexLabel, bx, by);
						g.fillText(vertexLabels[vCnt], bx, by);
                    
                	}
//System.out.println(vertexLabels[vCnt]);                    
                }
            }
        }
        
        
        
        
    }  // paintFacet  
    
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
    private boolean edgesLabeled()
    {   boolean labeled = false;
        for (int i = 0; i < numPoints; i++)
        {   if (edgeCodes[i] != 0)
                return true;
        }    
        return labeled;
    }    
    
    // draw edge by edge, only when non-filled and
    // some edgelabel != 0
// highlighting edges
// gebruik een shift, zeg 10
// highlighting: tel 10 op bij edgeLabels[i] i>=0
// kies kleuren via edgeLabels[i % 10]
// de-highlighting: als i / 10 >= 1 dan i = i - 10
// dan dikker tekenen: alles twee keer
// hoek > 45 graden een pixel naar rechts
// anders een pixel naar beneden
/*
tot nu toe 
0 default outline color
1 line color
2 plane color
hidden via normaal

extra
10 default outline color
11 line color
12 plane color
extern op hidden gezet

extra
20 default outline color
21 line color
22 plane color
highlighted en hidden via normaal

extra
30 default outline color
31 line color
32 plane color
highlighted en extern op hidden gezet

extra
40 default outline color
41 line color
42 plane color
extern op UNhidden gezet


  
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
                    {   //g.setColor(getHiddenOutlineColor(edgeColors[edgeCodes[i]]));
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i]]));
                    }        
                    else // not "hidden"
                    {    //g.setColor(edgeColors[edgeCodes[i]]);
                    	g.setStrokeStyle(edgeColors[edgeCodes[i]]);
                    }
                    hidden = !visFromD;    
                }
                // externally hidden
                else if ((edgeCodes[i] >= 10) && (edgeCodes[i] < 20))
                {   if (hiddenOutlineMode % 2 == 0)
            		{    //g.setColor(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 10]));
            			g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 10]));
            		}
                	else // no "hidden" color
                	{    //g.setColor(edgeColors[edgeCodes[i] % 10]);
                		g.setStrokeStyle(edgeColors[edgeCodes[i] % 10]);
                	}
                    hidden = true;    
                }    
                // hightlighted and hiding through normal
                else if ((edgeCodes[i] >= 20) && (edgeCodes[i] < 30))
                {   highlighted = true;
                	if (!visFromD &&
                        //(unitNormal.z <= - Vector3D.NZero) &&
                        (hiddenOutlineMode % 2 == 0)
                        )
                    {    
                        //g.setColor(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 20]));
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 20]));
                    }        
                    else // not "hidden"
                    {    //g.setColor(edgeColors[edgeCodes[i] % 20]);
                    	g.setStrokeStyle(edgeColors[edgeCodes[i] % 20]);
                    }
                    hidden = !visFromD;    
                }
                // highlighted and externally hidden
                else if ((edgeCodes[i] >= 30) && (edgeCodes[i] < 40))
                {   
                	if (hiddenOutlineMode % 2 == 0)
                    {    //g.setColor(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 30]));
                    	g.setStrokeStyle(getHiddenOutlineColor(edgeColors[edgeCodes[i] % 30]));
                    }
                    else // no "hidden" color
                    {    //g.setColor(edgeColors[edgeCodes[i] % 10]);
                    	g.setStrokeStyle(edgeColors[edgeCodes[i] % 10]);
                    }
                    highlighted = true;
                    hidden = true;
                }    
                // extern unhidden
                else if ((edgeCodes[i] >= 40) && (edgeCodes[i] < 50))
                {   //g.setColor(edgeColors[edgeCodes[i] % 40]);
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
                
// hier hightlighten!!!
// drawHighlightedLine
// drawHighlightedDashedLine
                // drawing
                if (!grafiek3D)
                {	
                	if (!hidden || (hiddenOutlineMode == 0))    
                	{	//g.drawLine(p.xpoints[i], p.ypoints[i],
                		//		   p.xpoints[(i+1) % p.npoints], 
                        //           p.ypoints[(i+1) % p.npoints]);
                	
                		g.beginPath();
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
                else // grafiek3D
                {	if (!normalHidden)
                	{	//g.setColor(getHiddenOutlineColor(outlineColor));
                		g.setStrokeStyle(getHiddenOutlineColor(outlineColor));
                	}
                	else
                	{	//g.setColor(outlineColor);
                		g.setStrokeStyle(outlineColor);
                	}
                	if (!hidden)    
                	{	//g.drawLine(p.xpoints[i], p.ypoints[i], p.xpoints[(i+1) % p.npoints], 
                        //           p.ypoints[(i+1) % p.npoints]);
                	
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
    
    public void drawDashedLine(Context2d g, double x1, double y1, double x2, double y2)
    {   int dis = (int) Math.round(pixDisD(x1, y1, x2, y2));
    
        boolean dashOn = true;
        double xStart, xEnd, yStart, yEnd;
        // points too close to dash
        if (dis <= DASH)
        {   //g.drawLine(x1, y1, x2, y2);
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
                {   //g.drawLine(xStart, yStart, xStart, 
                    //    Math.min(yStart + DASH, yEnd));
                    
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
                {   //g.drawLine(xStart, yStart, 
                    //    Math.min(xStart + DASH, xEnd), yStart);
                
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
//System.out.println("alpha = " + UF.format(alpha, 4));            
            double length = pixDisD(xStart, yStart, xEnd, yEnd);
            int steps = (int) Math.round(
                pixDisD(xStart, yStart, xEnd, yEnd) / DASH);
//System.out.println("steps = " + steps);
                
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
                {   //g.drawLine(xFrom, yFrom, xTo, yTo);
                	
                	g.beginPath();
                	g.moveTo(xFrom, yFrom);
                	g.lineTo(xTo, yTo);
                	g.stroke();

                }
            }
            
        }    
    }
    
    private double pixDis(int x1, int y1, int x2, int y2)
    {   return Math.sqrt((x1 - x2) * (x1 - x2) +
                         (y1 - y2) * (y1 - y2));
        
    }    
    
    private double pixDisD(double x1, double y1, double x2, double y2)
    {   return Math.sqrt((x1 - x2) * (x1 - x2) +
                         (y1 - y2) * (y1 - y2));
        
    }    
    
    
    public static CssColor getHiddenOutlineColor(CssColor c)    
    {   CssColor result = c;
        if (c.toString().equals(black.toString()))
        {    
        	return lightGray;

        }
// tijdelijk
        if (c.toString().equals(blue.toString()))
            return mediumBlue;
        if (c.equals(red))
        {   return lightRed;
            //return orange;
        }    
        if (c.toString().equals(brownRed.toString()))
        {   return lightRed;
//System.out.println("or");        	
        	//return orange;
        }	
        return result;
    }    
    
    // static methods saving memory
    // find shadowed color
    public static CssColor shadowColor(Facet3D f, boolean reverseNormal)
    {   // on x^2+y^2+z^2=1 with z>=0 -n.x-n.y+n.z has maximum sqrt(3) and
        // minimum -sqrt(2) for x^2=y^2=z^2=1/3 resp x=y=sqrt(2)/2, z=0
        // this should be >=0 and <= 1
        // see shadowGrayColor
        
// voor 2D facets aparte kleurmogelijkheid        
        if (Vector3D.equals(f.unitNormal, new Vector3D()))
            return CssColor.make(0,0,0);
            
// hier lichtrichting inbrengen            
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
		
        // .8*255=204 thus maximum 50+204=254
//      int red = 50 + (int)(f.color.getRed() * grayFactor * (8e-1d) );
//      int green = 50 + (int)(f.color.getGreen() * grayFactor * (8e-1d) );
//      int blue = 50 + (int)(f.color.getBlue() * grayFactor * (8e-1d) );
        
// nodig?
if (red > 255)
    red = 255;
if (green > 255)
    green = 255;
if (blue > 255)
    blue = 255;
    
    
	     return CssColor.make(red, green, blue);
        //return new Color(red, green, blue, f.color.getAlpha());
    }
    // find shadowed grey color, use reverse unitNormal
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
        // use this for contrast
        // grayFactor = Math.pow(grayFactor, 3);
            
            
        int gray = (int)(255 * grayFactor * (8e-1d));
// nodig?        
if (gray > 255)
    gray = 255;
if (gray < 0)
    gray = 0;
        
        return CssColor.make(gray, gray, gray);
    }
    
    public static void copyAttributes(Facet3D orig, Facet3D copy, boolean exact)
    {   copy.outlined = orig.outlined;
        copy.filled = orig.filled;
        copy.empty = orig.empty;
        copy.visible = orig.visible;
        copy.color = orig.color;
        copy.outlineColor = orig.outlineColor;
        copy.hiddenOutlineMode = orig.hiddenOutlineMode;
        copy.edgeColors = orig.edgeColors;
        
        //if (orig.numPoints == copy.numPoints)
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
    // check if this facet contains v as vertex
    // cannot be done by reference, v might belong to another object!
    // return -1 (no) or index of v in this facet
    public static int containsVertex(Facet3D f, Vector3D v)
    {   int index = -1;
        for (int i = 0; i < f.numPoints; i++)
        {   if (Vector3D.equals(v, f.points[i]))
               return i;
        }
        return index;
    }
    // necessary to find adjacent facets
    // check if this facet contains the directed segment v1->v2 as
    // a side, return -1 (no) or index of v1 in this facet
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
    // check if an edge of this facet contains the directed segment 
    // v1->v2 as a subset i.e. we have edgeStart->v1->v2->edgeEnd
    // return -1 (no) or index of the edge in this facet
    // assume v1->v2 is a segment
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
    
    // check if an edge of this facet contains the segment 
    // v1, v2 as a subset i.e. we have edgeStart->v1->v2->edgeEnd
    // or edgeStart->v2->v1->edgeEnd
    // return -1 (no) or index of the edge in this facet
    // also works for points i.e. v1==v2
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
    // FASTER, useful?
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
    
    // check if the point v in within the facet!
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
    // check if this facet equals Facet3D f in this sense:
    // both contain the same set of vertices AND the
    // directed vertex arrays are equal up to cyclic permutation
    // thus facets must have the same orientation
    // return -1 (not equal) or index of vertex in f that corresponds to
    // points[0] of this vertex
    // normally we compare by reference
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
    // check if these facets are each others reverse!!
    // return -1 (not equal) or index of vertex in f that corresponds to
    // points[0] of this vertex
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
    
/*
hier wordt een nieuw facet gemaakt (de vertices blijven dezelfde!)
beter?: draai alleen de vertices om
van de referentie naar Facet3D f wordt een lokale kopie gemaakt
dit is geen probleem zie Vector3D
*/
    // reverse the orientation of this facet
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
