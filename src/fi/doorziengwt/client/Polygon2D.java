package fi.doorziengwt.client;

/**
 * a polygon in the real plane, see class Polygon2D in Grafiek3DGWT
 * @author huub
  */

public class Polygon2D
{   double[] xpoints;
 double[] ypoints;
 int npoints;
 double baX, baY; // barycenter coordinates
 
 public Polygon2D(double[] xpts, double[] ypts, int npts) 
 {   xpoints = xpts;
     ypoints = ypts;
     npoints = npts;
 }
 
 boolean equals(double p1X, double p1Y, double p2X, double p2Y)
 {   return Math.sqrt((p1X - p2X) * (p1X - p2X) +
                      (p1Y - p2Y) * (p1Y - p2Y)) < Vector3D.NZero;
 }
 
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
 
 
 public boolean isInternal(double x, double y) 
 {
     
         findBarycenter();
         
         boolean result = true;
         
         // Walk the edges of the polygon
         for (int n = 0; n < npoints; n++) 
         {
             double xStart =  xpoints[n];
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
             double baPos = nX * baX + nY * baY 
                          - nX * xStart - nY * yStart;
             
             double pointPos = 
                 nX * x + nY * y - nX * xStart - nY * yStart;
                 
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
 
 public boolean isOutside(int edgeIndex, Polygon2D pB)
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
     // the position of the barycenter relative to the
     // line is given by the double(!)
     // nX * baX + nY * baY - nX * xStart - nY * yStart
     // which is either > 0 or < 0
     double baPos = nX * baX + nY * baY 
                  - nX * xStart - nY * yStart;
     boolean result = true;             
     for (int i = 0; i < pB.npoints; i++)
     {
         double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
                         - nX * xStart - nY * yStart;

         result = result &&
                  ((baPos > Vector3D.NZero) && 
                   (pointPos <= Vector3D.NZero)) ||
                  ((baPos < -Vector3D.NZero) && 
                   (pointPos >= -Vector3D.NZero));
         if (!result)
             return false;    
     }
     return result;
 }

 
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
     {
         double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
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

 public boolean areOnOneSide(int edgeIndex, 
         double firstX, double firstY,
         double nextX, double nextY)
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
 
 
 public boolean isSeparatedFrom(Polygon2D pB, boolean firstTry)
 {   // this (pA) een vlakje
     if (npoints >= 3) // pB vlakje of segment
     {    findBarycenter();
         // voldoende met 1 edge
         for (int eCnt = 0; eCnt < npoints; eCnt++)
         {   boolean outSideE = isOutside(eCnt, pB);
             if (outSideE)
                 return true;
         } // for
         // false result here
         if (firstTry)
             return pB.isSeparatedFrom(this, false);
         else
             return false;
     } // if (npoints >= 3)
     // pA een segment
     else if (npoints == 2)
     {   // pB een vlakje, use only one side
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
         // pB ook een segment
         else if (pB.npoints == 2)
         {   
             
             // kijk of de segmenten raken
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
