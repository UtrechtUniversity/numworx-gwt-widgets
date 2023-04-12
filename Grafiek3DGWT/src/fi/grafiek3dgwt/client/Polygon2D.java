package fi.grafiek3dgwt.client;

/**
 * class representing a plane polygon whose points have real coordinates 
 * @author huub
 */
public class Polygon2D
{   /**
	 * x-coordinates of the points 	
	 */
	double[] xpoints;
	/**
	 * y-coordinates of the points
	 */
    double[] ypoints;
    /**
     * number of points
     */
    int npoints;
    /**
     * x-coordinate of barycenter
     */
    double baX; 
    /**
     * y-coordinate of barycenter
     */
    double baY; 
    
    /**
     * constructor
     * @param xpts x-coordinates of the points
     * @param ypts y-coordinates of the points
     * @param npts number of points
     */
    public Polygon2D(double[] xpts, double[] ypts, int npts) 
    {   xpoints = xpts;
        ypoints = ypts;
        npoints = npts;
    }
    /**
     * check id the real points (p1X,p1Y) and (p2X,p2Y) 
     * @param p1X x-coordinate of first point
     * @param p1Y y-coordinate of first point
     * @param p2X x-coordinate of second point
     * @param p2Y y-coordinate of second point
     * @return true/false
     */
    boolean equals(double p1X, double p1Y, double p2X, double p2Y)
    {   return Math.sqrt((p1X - p2X) * (p1X - p2X) +
                         (p1Y - p2Y) * (p1Y - p2Y)) < Vector3D.NZero;
    }
    /**
     * set the barycenter coordinates of this Polygon2D
     */
    void findBarycenter()
    {   baX = 0;
        baY = 0;
        for (int i = 0; i < npoints; i++)
        {   baX += xpoints[i];
            baY += ypoints[i];
        }    
        baX /= npoints;
        baY /= npoints;
    }    
    /**
     * check if the point (x,y) is inside this Polygon2D which is assumed 
     * to have 3 different non-collinear points 
     * @param x x-coordinate of check point
     * @param y y-coordinate of check point
     * @return true/false
     */
    public boolean isInternal(double x, double y) 
    {   findBarycenter();
        boolean result = true;
        // Walk the edges of the polygon
        for (int n = 0; n < npoints; n++) 
        {   double xStart =  xpoints[n];
            double yStart = ypoints[n];
            double xEnd = xpoints[(n + 1) % npoints];
            double yEnd = ypoints[(n + 1) % npoints];
            // line through start and end has directional
            // vector (xEnd-xStart, yEnd-yStart)
            // thus normal vector 
            // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
            // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
            double dx = xEnd-xStart;
            double dy = yEnd-yStart;
            double nX = 0;
            double nY = 0;
            if (Math.abs(dx) > Vector3D.NZero)
            {   nX = dy;
                nY = -dx;
            }
            else // points supposed different
            {   nX = -dy;
                nY = dx;
            }
            // equation of the line is
            // nX * X + nY * Y = nX * xStart + nY * yStart
            // the position of the barycenter relative to the
            // line is given by the double(!)
            // nX * baX + nY * baY - nX * xStart - nY * yStart
            // which is either > 0 or < 0
            double baPos = nX * baX + nY * baY - nX * xStart - nY * yStart;
            double pointPos = nX * x + nY * y - nX * xStart - nY * yStart;
            result = result && 
               (
                (
                 ((baPos > Vector3D.NZero) && (pointPos > Vector3D.NZero)) ||
                 ((baPos < -Vector3D.NZero) && (pointPos < -Vector3D.NZero))
                )
               );
            // exit if (x,y) not on same side of ba for some edge    
            if (!result)
                return false;
            } // for
            return result;
    }
    /**
     * determine if the line through the edge with index edgeIndex of this Polygon2D does not intersect
     * Polygon2D pB
     * @param edgeIndex index of edge
     * @param pB the Polygon2D
     * @return true (no intersection)/false (intersection)
     */
    public boolean isOutside(int edgeIndex, Polygon2D pB)
    {   double xStart = xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the barycenter relative to the
        // line is given by the double(!)
        // nX * baX + nY * baY - nX * xStart - nY * yStart
        // which is either > 0 or < 0
        double baPos = nX * baX + nY * baY 
                     - nX * xStart - nY * yStart;
        boolean result = true;
        // true if all points of pB and the barycenter of pB
        // are on the same side of the line
        for (int i = 0; i < pB.npoints; i++)
        {   double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
                            - nX * xStart - nY * yStart;
            result = result &&
                     ((baPos > -Vector3D.NZero) && 
                      (pointPos > -Vector3D.NZero)) ||
                     ((baPos < Vector3D.NZero) && 
                      (pointPos < Vector3D.NZero));
            if (!result)
                return false;    
        }
        return result;
    }
    /**
     * determine if the all points of Polygon2D lie on the same side of the line
     * through the edge with index edgeIndex of this Polygon2D
     * @param edgeIndex index of edge
     * @param pB the Polygon2D
     * @return true (same side)/false (not same side)
     */
    public boolean isOnOneSide(int edgeIndex, Polygon2D pB)
    {   double xStart =  xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the first point of pB relative to the
        // line is given by the double(!)
        // nX * pB.xpoints[0] + nY * pB.ypoints[0]
        //  - nX * xStart - nY * yStart;
        // which is either > 0 or < 0
        double firstPointPos = nX * pB.xpoints[0] + nY * pB.ypoints[0]
                             - nX * xStart - nY * yStart;
        boolean result = true;             
        for (int i = 1; i < pB.npoints; i++)
        {   double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
                            - nX * xStart - nY * yStart;
            result = result &&
                     ((firstPointPos > -Vector3D.NZero) && 
                      (pointPos > -Vector3D.NZero)) ||
                     ((firstPointPos < Vector3D.NZero) && 
                      (pointPos < Vector3D.NZero));
            if (!result)
                return false;    
        }
        return result;
    }
    /**
     * determine if the points (firstX,firstY) and (nextX,nextY) lie on the same side of the line
     * through the edge with index edgeIndex of this Polygon2D
     * @param edgeIndex edgeIndex index of edge
     * @param firstX x-coordinate first point
     * @param firstY y-coordinate first point
     * @param nextX x-coordinate second point
     * @param nextY y-coordinate second point
     * @return true (same side)/false (not same side)
     */
    public boolean areOnOneSide(int edgeIndex, 
            double firstX, double firstY,
            double nextX, double nextY)
    {   double xStart = xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the first point of pB relative to the
        // line is given by the double(!)
        // nX * pB.xpoints[0] + nY * pB.ypoints[0]
        //  - nX * xStart - nY * yStart;
        // which is either > 0 or < 0
        double firstPointPos = nX * firstX + nY * firstY
                             - nX * xStart - nY * yStart;
        double nextPointPos = nX * nextX + nY * nextY
                            - nX * xStart - nY * yStart;
        boolean result = ((firstPointPos > -Vector3D.NZero) && 
                          (nextPointPos > -Vector3D.NZero)) ||
                         ((firstPointPos < Vector3D.NZero) && 
                          (nextPointPos < Vector3D.NZero));
        return result;
    }
    /**
     * check if Polygon2D pB is separated from this Polygon2D (i.e. no intersections of edges) 
     * @param pB the Polygon2D
     * @param firstTry check twice if firstTry == true 
     * @return true/false
     */
    public boolean isSeparatedFrom(Polygon2D pB, boolean firstTry)
    {   // this Polygon2D a plane 
        if (npoints >= 3) // pB plane or segment
        {   findBarycenter();
        	boolean outSideE = true;
            for (int eCnt = 0; eCnt < npoints; eCnt++)
            {   outSideE = outSideE && isOutside(eCnt, pB);
            } // for
            if (outSideE)
                return true;
            // false result here
            if (firstTry)
                return pB.isSeparatedFrom(this, false);
            else
                return false;
        } // if (npoints >= 3)
        // this Polygon2D a segment
        else if (npoints == 2)
        {   // pB a plane
            if (pB.npoints >= 3)
            {   boolean result = isOnOneSide(0, pB);
                if (result)
                    return result;
                else
                {   if (firstTry)
                        return pB.isSeparatedFrom(this, false);
                    else
                        return false;
                }    
            }
            // pB also a segment
            else if (pB.npoints == 2)
            {   // segments touch in end points
                if (equals(xpoints[0], ypoints[0],
                           pB.xpoints[0], pB.ypoints[0]) || 
                    equals(xpoints[0], ypoints[0],
                           pB.xpoints[1], pB.ypoints[1]) || 
                    equals(xpoints[1], ypoints[1],
                           pB.xpoints[0], pB.ypoints[0]) || 
                    equals(xpoints[1], ypoints[1],
                           pB.xpoints[1], pB.ypoints[1])
                   )
                    return false;                
                boolean result = areOnOneSide(0,
                    pB.xpoints[0], pB.ypoints[0],
                    pB.xpoints[1], pB.ypoints[1]);
                if (result)    
                    return result;
                else
                {   if (firstTry)
                        return pB.isSeparatedFrom(this, false);
                    else    
                        return false;
                }    
            }
        }    
        return false;
    }
} // class Polygon2D  
