package fi.verknippengwt.client;

import java.util.*;
//import java.awt.Point;
//import java.io.Serializable;

public class RealPoint //implements Serializable
{	
    // a small number
    public static final double NZero = 1e-5d; //1e-9d;
    // a big number
    public static final double Big = 1e+9d;
    // a big negative number
    public static final double MBig = -1e+9d;
    
    public static RealPoint NODEF = new RealPoint(Big, Big);
    public static RealPoint NOPOS = new RealPoint(Big, MBig);
    public static RealPoint NONE = new RealPoint(MBig, MBig);    

	public double x, y;

	// constructor 1
	public RealPoint(double x, double y)
	{	this.x = x;
		this.y = y;
	}
	// constructor 2
	public RealPoint(RealPoint rp)
	{	x = rp.x;
		y = rp.y;
	}

	// constructor 3
	// constructor 2
	public RealPoint(Point rp)
	{	x = rp.x;
		y = rp.y;
	}
	
	public void translate(double dx, double dy)
	{	x += dx;
		y += dy;
	}

	public Point toPoint()
	{	return new Point((int) Math.round(x), (int) Math.round(y));
	}

    // redefine for method contains in Vector     
    // equality of this RealPoint and RealPoint u in Euclidean metric NZero
    public boolean equals(Object obj)
    {    if (obj instanceof RealPoint)
             return distance((RealPoint) obj) < NZero;
         return false;    
    }

	public boolean screenEquals(RealPoint u)
	{	double ix = (int) Math.round(x);
    	double iy = (int) Math.round(y);
		double iux = (int) Math.round(u.x);
		double iuy = (int) Math.round(u.y);    	
		return (ix == iux) && (iy == iuy);
	}

    // distance between this RealPoint and RealPoint u
    public double distance(RealPoint u)
    {   return Math.sqrt((x - u.x) * (x - u.x) +
                         (y - u.y) * (y - u.y));
    }

    // distance between this RealPoint and RealPoint u
    // na tekenen op het scherm
    public double screenDistance(RealPoint u)
    {   double ix = (int) Math.round(x);
    	double iy = (int) Math.round(y);
		double iux = (int) Math.round(u.x);
		double iuy = (int) Math.round(u.y);    	
    	return Math.sqrt((ix - iux) * (ix - iux) +
                         (iy - iuy) * (iy - iuy));
    }

	public double length()
	{	return Math.sqrt(x * x + y * y);
	}

    // inproduct between this RealPoint and RealPoint u
	public double innerProduct(RealPoint u)
	{	return (x * u.x + y * u.y);
	}

	// nieuwe unit vector in dezelfde richting
	public RealPoint normalize()
	{	double length = length();
		if (length > NZero)
			return new RealPoint(x / length, y / length);
		else
			return new RealPoint(x, y);	
	}

	// nieuwe vector ontstaan door this RealPoint
	// dh graden om punt (cx, cy) tegen de klok in te draaien
	public RealPoint rotate(double dh, double cx, double cy)
	{	// radialen
		double cos = Math.cos(dh * Math.PI / 180);
		double sin = Math.sin(dh * Math.PI / 180);
		double nx = x - cx;
		double ny = y - cy;
		return new RealPoint(cos * nx + sin * ny + cx,
							 cos * ny - sin * nx + cy);
	}

	// projecteer deze vector op RealPoint u
	public RealPoint projectOn(RealPoint u)
	{	// inproduct1	
		double in1 = innerProduct(u);
		// inproduct2	
		double in2 = u.innerProduct(u);
		return new RealPoint((in1 / in2) * u.x, (in1 / in2) * u.y);

	}

	// hoek in graden tussen deze vector en RealPoint u
	// positief is tegen de klok in
	// beide vectoren moeten lengte >= NZero hebben
	public double getAngle(RealPoint u)
	{	double l = length();
		double ul = u.length();
		if ((l < NZero) || (ul < NZero))
			return 0;
		// normalize
		RealPoint n = normalize();
		RealPoint un = u.normalize();
		// cosinus hoek
		double cosDAngle = n.innerProduct(un);
		// afronden!!!
		if (cosDAngle > 1)
			cosDAngle = 1;
		else if (cosDAngle < -1)
			cosDAngle = -1;	 				   
		// hoek in radialen
		double dAngleRad = Math.acos(cosDAngle);
		// draai vector n over de hoek dAngleRad
		// tegen de klok in
		double checkX = Math.cos(dAngleRad) * n.x -
						Math.sin(dAngleRad) * n.y;
		double checkY = Math.sin(dAngleRad) * n.x +
						Math.cos(dAngleRad) * n.y;				
		// check if this gives un
		if ((Math.abs(un.x - checkX) > RealPoint.NZero) ||
			(Math.abs(un.y - checkY) > RealPoint.NZero))
		{	//dAngleRad = - dAngleRad;
		}	
		else
		{	dAngleRad = - dAngleRad;
		}
		// draaihoek in graden
		double dAngle = dAngleRad * 360 / (2 * Math.PI);
		return dAngle;
	}
	

	// kijk of dit RealPoint op de lijn door p1 en p2 ligt
	public boolean isOnLine(RealPoint p1, RealPoint p2)
	{	// vector p1->this
		RealPoint dir1 = new RealPoint(x - p1.x, y - p1.y);
		RealPoint dir2 = new RealPoint(x - p2.x, y - p2.y);
		RealPoint proj;
		RealPoint result;
		if (dir1.length() > dir2.length())
		{	RealPoint dir = new RealPoint(p2.x - p1.x, p2.y - p1.y);
			proj = dir1.projectOn(dir);
			result = new RealPoint(p1.x + proj.x, p1.y + proj.y);			
		}
		else
		{	RealPoint dir = new RealPoint(p1.x - p2.x, p1.y - p2.y);
			proj = dir2.projectOn(dir);
			result = new RealPoint(p2.x + proj.x, p2.y + proj.y);			
		}
		return distance(result) < NZero;
	}

	// kijk of dit RealPoint op de lijn door p1 en p2 ligt
	// geef het dichtstbijzijnde punt terug
	public RealPoint pointOnLine(RealPoint p1, RealPoint p2)
	{	// vector p1->this
		RealPoint dir1 = new RealPoint(x - p1.x, y - p1.y);
		RealPoint dir2 = new RealPoint(x - p2.x, y - p2.y);
		RealPoint proj;
		RealPoint result;
		if (dir1.length() > dir2.length())
		{	RealPoint dir = new RealPoint(p2.x - p1.x, p2.y - p1.y);
			proj = dir1.projectOn(dir);
			result = new RealPoint(p1.x + proj.x, p1.y + proj.y);
		}
		else
		{	RealPoint dir = new RealPoint(p1.x - p2.x, p1.y - p2.y);
			proj = dir2.projectOn(dir);
			result = new RealPoint(p2.x + proj.x, p2.y + proj.y);
		}
		if (distance(result) < 2 * NZero)
			return result;
		else
			return null;	
	}

	// vindt het punt op de lijn door p1 en p2 zo dicht mogelijk bij
	// dit RealPoint
	public RealPoint closestPointOnLine(RealPoint p1, RealPoint p2)
	{	// vector p1->this
		RealPoint dir1 = new RealPoint(x - p1.x, y - p1.y);
		// vector p2->this
		RealPoint dir2 = new RealPoint(x - p2.x, y - p2.y);
		RealPoint proj;
		RealPoint result;
		if (dir1.length() > dir2.length())
		{	RealPoint dir = new RealPoint(p2.x - p1.x, p2.y - p1.y);
			proj = dir1.projectOn(dir);
			result = new RealPoint(p1.x + proj.x, p1.y + proj.y);
		}
		else
		{	RealPoint dir = new RealPoint(p1.x - p2.x, p1.y - p2.y);
			proj = dir2.projectOn(dir);
			result = new RealPoint(p2.x + proj.x, p2.y + proj.y);
		}
		return result;
	}
	

	// kijk of dit RealPoint op het segment p1-p2 ligt
	public boolean isOnSegment(RealPoint p1, RealPoint p2)
	{	// dit moet op de lijn liggen
		RealPoint onLine = pointOnLine(p1, p2);
		if (onLine == null)
			return false;
		// onLine niet null	
		double dis1 = onLine.distance(p1);
		double dis2 = onLine.distance(p2);
		return (dis1 + dis2 - p1.distance(p2)) < (2 * NZero);
	}
	// later pointOnSegment, nodig?

	// kijk of dit RealPoint op de cirkel met mp c en straal rad ligt
	public boolean isOnCircle(RealPoint c, double rad)
	{	// vector c->this
		RealPoint vrad = new RealPoint(x - c.x, y - c.y);
		if (vrad.length() < NZero)
			return false;
		// dit kan nu
		RealPoint vradn = vrad.normalize();	
		RealPoint circPoint = new RealPoint(c.x + rad * vradn.x, c.y + rad * vradn.y);
//System.out.println("" + distance(circPoint));		
		return distance(circPoint) < NZero;
	}

	// dit RealPoint ligt op segment end1-end2 AANNNAME!
	// het segment wordt newEnd1-newEnd2
	// bereken de nieuwe positie van dit RealPoint
	// dat ligt dan vanzelf op newEnd1-newEnd2
	// het werkt ook als end1,end2,newEnd1,newEnd2
	// corresponderende puntenparen zijn op het oude
	// resp. nieuwe segment
	public RealPoint findNewPos(RealPoint end1, RealPoint end2,
								RealPoint newEnd1, RealPoint newEnd2)
	{	// richtingsvector end1->this RealPoint
		RealPoint e1This = new RealPoint(x - end1.x, y - end1.y);
		// richtingsvector end2->this RealPoint
		RealPoint e2This = new RealPoint(x - end2.x, y - end2.y);
		if (e1This.length() > e2This.length())
		{	// richtingsvector oude segment end1->end2
			RealPoint dir = new RealPoint(end2.x - end1.x, end2.y - end1.y);
			// richtingsvector nieuwe segment newEnd1->newEnd2
			RealPoint newDir = new RealPoint(newEnd2.x - newEnd1.x, newEnd2.y - newEnd1.y);	
			// normalize
			RealPoint diru = dir.normalize();
			RealPoint newDiru = newDir.normalize();
			double lambda = e1This.innerProduct(diru);			
			return new RealPoint(newEnd1.x + lambda * newDiru.x,
								 newEnd1.y + lambda * newDiru.y);
		}
		else
		{	// richtingsvector oude segment end2->end1
			RealPoint dir = new RealPoint(end1.x - end2.x, end1.y - end2.y);
			// richtingsvector nieuwe segment newEnd2->newEnd1
			RealPoint newDir = new RealPoint(newEnd1.x - newEnd2.x, newEnd1.y - newEnd2.y);	
			// normalize
			RealPoint diru = dir.normalize();
			RealPoint newDiru = newDir.normalize();
			double lambda = e2This.innerProduct(diru);			
			return new RealPoint(newEnd2.x + lambda * newDiru.x,
								 newEnd2.y + lambda * newDiru.y);
			
		}
	}								

	public static Vector intersectCircles(RealPoint c1, double r1, 
										  RealPoint c2, double r2)
	{	Vector result = new Vector();
		// equation circle 1 is (X-c1.x)^2+(Y-c1.y)^2=r1^2	
		// equation circle 2 is (X-c2.x)^2+(Y-c2.y)^2=r2^2	
		// expand
		// circle 1 X^2-2*c1.x*X+c1.x^2+Y^2-2*c1.y*Y+c1.y^2=r1^2
		// circle 2 X^2-2*c2.x*X+c2.x^2+Y^2-2*c2.y*Y+c2.y^2=r2^2
		// 1 minus 2:
		// 2*(c2.x-c1.x)*X+2*(c2.y-c1.y)*Y=r1^2-r2^2+c2.x^2-c1.x^2+c2.y^2-c1.y^2
		// no solutions or infinite number
		if ((Math.abs(c2.x - c1.x) < NZero) && (Math.abs(c2.y - c1.y) < NZero))
			return result; 
		double norx = 2 * (c2.x - c1.x);
		double nory = 2 * (c2.y - c1.y);
		double co = r1 * r1 - r2 * r2 + c2.x * c2.x - c1.x * c1.x + 
					c2.y * c2.y - c1.y * c1.y;
		double A, B, C;				
		if (Math.abs(c2.x - c1.x) < NZero)
		{	// express Y in X and put in equation circle 1
			// coefficient X^2
			A = 1 + norx * norx / (nory * nory);
			// coefficient X
			B = - 2 * co * norx / (nory * nory) +
			    2 * c1.y * norx / nory - 2 * c1.x;
			// constant
			C = co * co / (nory * nory) - 2 * c1.y * co / nory +
			    c1.x * c1.x + c1.y * c1.y - r1 * r1;	
			// discrimimant
			double dis = B * B - 4 * A * C;
			// 2 solutions for X
			if (dis > NZero)
			{	double x1 = (- B + Math.sqrt(dis)) / (2 * A);
				double y1 = (co - norx * x1) / nory;
				result.addElement(new RealPoint(x1, y1));
				double x2 = (- B - Math.sqrt(dis)) / (2 * A);
				double y2 = (co - norx * x2) / nory;
				result.addElement(new RealPoint(x2, y2));
			}
			// 1 solution for X
			else if (dis >= 0)
			{	double x = - B / (2 * A);
				double y = (co - norx * x) / nory;
				result.addElement(new RealPoint(x, y));
			}
			// else no solutions			    
		}
		else // express X in Y and put in equation circle 1
		{	// coefficient Y^2
			A = 1 + nory * nory / (norx * norx);
			// coefficient Y
			B = - 2 * co * nory / (norx * norx) +
			    2 * c1.x * nory / norx - 2 * c1.y;
			// constant
			C = co * co / (norx * norx) - 2 * c1.x * co / norx +
			    c1.x * c1.x + c1.y * c1.y - r1 * r1;	
			// discrimimant
			double dis = B * B - 4 * A * C;
			// 2 solutions for Y
			if (dis > NZero)
			{	double y1 = (- B + Math.sqrt(dis)) / (2 * A);
				double x1 = (co - nory * y1) / norx;
				result.addElement(new RealPoint(x1, y1));
				double y2 = (- B - Math.sqrt(dis)) / (2 * A);
				double x2 = (co - nory * y2) / norx;
				result.addElement(new RealPoint(x2, y2));
			}
			// 1 solution for Y
			else if (dis >= 0)
			{	double y = - B / (2 * A);
				double x = (co - nory * y) / norx;
				result.addElement(new RealPoint(x,y));
			}
			// else no solutions
		}	
		return result;
	}

	public static Vector intersectCircleAndLine(RealPoint c, double r, 
									            RealPoint e1, RealPoint e2)
	{	Vector result = new Vector();
		// richtingsvector lijn
		RealPoint dir = new RealPoint(e2.x - e1.x, e2.y - e1.y);
		// normaalvector lijn
		RealPoint nor;
		if (Math.abs(dir.x) < NZero)
		{	nor = new RealPoint(- dir.y, dir.x);
		}
		else
			nor = new RealPoint(dir.y, - dir.x);
		// equation line is nor.x*X+nor.y*Y=(nor,e1)=co
		// equation circle is (X-c.x)^2+(Y-c.y)^2=r^2
		// i.e. X^2-2c.x*X+c.x^2+Y^2-2c.y*y+c.y^2=r^2
		double co = nor.innerProduct(e1);
		// express Y in X
		double A, B, C;
		if (Math.abs(nor.x) < NZero)
		{	// express Y in X and put in equation circle
			// coefficient X^2
			A = 1 + nor.x * nor.x / (nor.y * nor.y);
			// coefficient X
			B = - 2 * co * nor.x / (nor.y * nor.y) +
			    2 * c.y * nor.x / nor.y - 2 * c.x;
			// constant
			C = co * co / (nor.y * nor.y) - 2 * c.y * co / nor.y +
			    c.x * c.x + c.y * c.y - r * r;	
			// discrimimant
			double dis = B * B - 4 * A * C;
			// 2 solutions for X
			if (dis > NZero)
			{	double x1 = (- B + Math.sqrt(dis)) / (2 * A);
				double y1 = (co - nor.x * x1) / nor.y;
				result.addElement(new RealPoint(x1, y1));
				double x2 = (- B - Math.sqrt(dis)) / (2 * A);
				double y2 = (co - nor.x * x2) / nor.y;
				result.addElement(new RealPoint(x2, y2));
			}
			// 1 solution for X
			else if (dis >= 0)
			{	double x = - B / (2 * A);
				double y = (co - nor.x * x) / nor.y;
				result.addElement(new RealPoint(x, y));
			}
			// else no solutions			    
		} 
		else // express X in Y and put in equation circle
		{	// coefficient Y^2
			A = 1 + nor.y * nor.y / (nor.x * nor.x);
			// coefficient Y
			B = - 2 * co * nor.y / (nor.x * nor.x) +
			    2 * c.x * nor.y / nor.x - 2 * c.y;
			// constant
			C = co * co / (nor.x * nor.x) - 2 * c.x * co / nor.x +
			    c.x * c.x + c.y * c.y - r * r;	
			// discrimimant
			double dis = B * B - 4 * A * C;
			// 2 solutions for Y
			if (dis > NZero)
			{	double y1 = (- B + Math.sqrt(dis)) / (2 * A);
				double x1 = (co - nor.y * y1) / nor.x;
				result.addElement(new RealPoint(x1, y1));
				double y2 = (- B - Math.sqrt(dis)) / (2 * A);
				double x2 = (co - nor.y * y2) / nor.x;
				result.addElement(new RealPoint(x2, y2));
			}
			// 1 solution for Y
			else if (dis >= 0)
			{	double y = - B / (2 * A);
				double x = (co - nor.y * y) / nor.x;
				result.addElement(new RealPoint(x,y));
			}
			// else no solutions
		}	
		return result;
	}

	// als samenvallend of parallel: geen punt		
	public static Vector intersectLines(RealPoint d1, RealPoint d2, 
								        RealPoint e1, RealPoint e2)
	{	Vector result = new Vector();
		// richtingsvector lijn 1
		RealPoint dir1 = new RealPoint(d2.x - d1.x, d2.y - d1.y);
		// normaalvector lijn 1
		RealPoint nor;
		if (Math.abs(dir1.x) < NZero)
		{	nor = new RealPoint(- dir1.y, dir1.x);
		}
		else
			nor = new RealPoint(dir1.y, - dir1.x);
		double co = nor.innerProduct(d1);
		// equation line 1 is nor.x*X+nor.y*Y=(nor,d1)=co
		// richtingsvector lijn 2
		RealPoint dir2 = new RealPoint(e2.x - e1.x, e2.y - e1.y);
		// steunvector lijn 2 is e1
		// oplossing: lambda*(nor.x*dir2.x+nor.y*dir2.y)=co-nor.x*e1.x-nor.y*e1.y
		double A = nor.x * dir2.x + nor.y * dir2.y;
		double B = co - nor.x * e1.x - nor.y * e1.y;
		if (Math.abs(A) > NZero)
		{	double lambda = B / A;
			result.addElement(new RealPoint(e1.x + lambda * dir2.x,
											e1.y + lambda * dir2.y));
		}
	
		return result;
	}

	// als in elkaars verlengde of deels samenvallend: geen punt
	// parallel natuurlijk ook niet
	public static RealPoint intersectSegments(RealPoint d1, RealPoint d2, 
								              RealPoint e1, RealPoint e2)
	{	RealPoint segResult = null;
		Vector lineResult = intersectLines(d1, d2, e1, e2);
		if (lineResult.size() == 0)
		{	return segResult;
		}
		else
		{	RealPoint lPoint = (RealPoint) lineResult.elementAt(0);
			if (lPoint.isOnSegment(d1, d2) && lPoint.isOnSegment(e1, e2))
			{	segResult = lPoint;
			}				
		}
		return segResult;
	}							           


	public double round(double d, int numDecs)
	{	return Math.round(d * Math.pow(10, numDecs)) / Math.pow(10, numDecs);
	}

	public String toString()
	{	return "(" + round(x, 9) + "," + round(y, 9) + ")";
	}
}

