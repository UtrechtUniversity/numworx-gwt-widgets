package fi.verknippengwt.client;

//import java.awt.*;
import java.util.*;
//import java.io.Serializable;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class KnipPolygon2
{	
	DrawingPanel2 owner;
	
	int aantalPunten;
	Point[] intPoints;
	RealPoint[] realPoints;
	Polygon intPolygon;
	
	RealPoint rotationPoint = null;

	final int TOPLEFT = 0;
	final int TOPRIGHT = 1;
	final int BOTTOMRIGHT = 2;
	final int BOTTOMLEFT = 3;
	final int TOP = 4;
	final int RIGHT = 5;
	final int BOTTOM = 6;
	final int LEFT = 7;
	
	RealPoint labelPoint = null;
	Rectangle labelRect = null;
	boolean labelVisible = false;
	int labelAlign = LEFT;

	int labelPos = TOP;
	int oppervlakte = 0;
	
	static int labelWidth;
	static int labelHeight;

	static final int CENTER = 0;
	static final int LEFTAL = 1;
	static final int RIGHTAL = 2;
	
	// constructor voor ingelezen figuur
	// de coordinaten zijn grid-coordinaten!!
	public KnipPolygon2(DrawingPanel2 o, Vector intPts, int align)
	{	owner = o;
	
		labelWidth = owner.labelWidth;
		labelHeight = owner.labelHeight;
		
	
		// opschonen voor dubbele punten
		Vector newIntPts = new Vector();
		for (int ipCnt = 0; ipCnt < intPts.size(); ipCnt++)
		{	Point iPoint = (Point) intPts.elementAt(ipCnt);
			if (!newIntPts.contains(iPoint))
				newIntPts.addElement(iPoint);
		}
		// opschonen voor collineariteit
		Vector cleanedPts = cleanPoints(newIntPts);
		aantalPunten = cleanedPts.size();
		intPoints = new Point[aantalPunten];
		realPoints = new RealPoint[aantalPunten];
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)	
		{	Point aPoint = (Point) cleanedPts.elementAt(pCnt);
			intPoints[pCnt] = new Point(
				aPoint.x * owner.gridSize, aPoint.y  * owner.gridSize);
			realPoints[pCnt] = new RealPoint(
				aPoint.x * owner.gridSize, aPoint.y * owner.gridSize);
		}
		maakIntPolygon();
		if (align == CENTER)
			centreer();
		else if (align == LEFTAL)
			links();
		else if (align == RIGHTAL)
			rechts();		
	}

	// overloaded
	// constructor voor knippen
	public KnipPolygon2(Vector realPts, DrawingPanel2 o)
	{	owner = o;
	
		// hier zijn geen dubbele punten	
		// opschonen voor collineariteit	
		Vector cleanedPts = cleanVertices(realPts);
		aantalPunten = cleanedPts.size();
		intPoints = new Point[aantalPunten];
		realPoints = new RealPoint[aantalPunten];
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)	
		{	RealPoint rPoint = (RealPoint) cleanedPts.elementAt(pCnt);
			intPoints[pCnt] = rPoint.toPoint(); 
			// de aangeboden punten zijn al nieuw	
			realPoints[pCnt] = rPoint;
		}
		maakIntPolygon();
	}	
	
	public Vector cleanVertices(Vector vertices)
	{	int num = vertices.size();
		Vector cleanedVertices = new Vector();
		for (int vCnt = 0; vCnt < num; vCnt++)
		{	RealPoint aVertex = 
				(RealPoint) vertices.elementAt(vCnt % num);
			RealPoint aVertexPlusOne = 
				(RealPoint) vertices.elementAt((vCnt + 1) % num);
			RealPoint aVertexPlusTwo = 
				(RealPoint) vertices.elementAt((vCnt + 2) % num);
			if (!aVertexPlusOne.isOnSegment(aVertex, aVertexPlusTwo))
				cleanedVertices.addElement(aVertexPlusOne);
		}
		return cleanedVertices;
	}

	public Vector cleanPoints(Vector points)
	{	int num = points.size();
		Vector cleanedPoints = new Vector();
		for (int vCnt = 0; vCnt < num; vCnt++)
		{	Point aPoint = 
				(Point) points.elementAt(vCnt % num);
			Point aPointPlusOne = 
				(Point) points.elementAt((vCnt + 1) % num);
			Point aPointPlusTwo = 
				(Point) points.elementAt((vCnt + 2) % num);
			RealPoint aVertex = new RealPoint(aPoint);
			RealPoint aVertexPlusOne = new RealPoint(aPointPlusOne);
			RealPoint aVertexPlusTwo = new RealPoint(aPointPlusTwo);	
			if (!aVertexPlusOne.isOnSegment(aVertex, aVertexPlusTwo))
				cleanedPoints.addElement(aPointPlusOne);
		}
		return cleanedPoints;
	}
	
	// altijd nodig
	public void maakIntPolygon()
	{	intPolygon = new Polygon();
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	intPolygon.addPoint(intPoints[pCnt].x, intPoints[pCnt].y);
		}
	}
	
	// alleen voor begin?
	public void centreer()
	{	Rectangle bBox = intPolygon.getBounds();
		// space in grid-units
		int hSpace = (owner.breedte / owner.gridSize - bBox.width / owner.gridSize) / 2;
		int vSpace = (owner.hoogte / owner.gridSize - bBox.height / owner.gridSize) / 2;
		translate(hSpace * owner.gridSize - bBox.x, 
				  vSpace * owner.gridSize - bBox.y);					  
	}	

	public void links()
	{	Rectangle bBox = intPolygon.getBounds();
		// space in grid-units
		int hSpace = owner.breedte / owner.gridSize / 2 - bBox.width / owner.gridSize - 1;
		int vSpace = (owner.hoogte / owner.gridSize - bBox.height / owner.gridSize) / 2;
		translate(hSpace * owner.gridSize - bBox.x, 
				  vSpace * owner.gridSize - bBox.y);					  
	}	
	
	public void rechts()
	{	Rectangle bBox = intPolygon.getBounds();
		// space in grid-units
		int hSpace = owner.breedte / owner.gridSize / 2 + 1;
					//(owner.getSize().width / owner.gridSize -
					 // bBox.width / owner.gridSize) - 1;
		int vSpace = (owner.hoogte / owner.gridSize - bBox.height / owner.gridSize) / 2;
		translate(hSpace * owner.gridSize - bBox.x, 
				  vSpace * owner.gridSize - bBox.y);					  
	}	
	
	// in schermcoordinaten!
	public void translate(int deltaX, int deltaY)
	{	for (int pCnt = 0; pCnt < aantalPunten; pCnt++)	
		{	intPoints[pCnt].translate(deltaX, deltaY);
			realPoints[pCnt].translate(deltaX, deltaY);
		}
		if (rotationPoint != null)
			rotationPoint.translate(deltaX, deltaY);
		maakIntPolygon();

	}
	
	// overloaded
	public void translate(double dX, double dY)
	{	for (int pCnt = 0; pCnt < aantalPunten; pCnt++)	
		{	//intPoints[pCnt].translate(deltaX, deltaY);
			realPoints[pCnt].translate(dX, dY);
			intPoints[pCnt] = realPoints[pCnt].toPoint();
		}
		if (rotationPoint != null)
			rotationPoint.translate(dX, dY);
		maakIntPolygon();
	}
	
	
	public RealPoint getBaryCenter()
	{	double bx = 0;
		double by = 0;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	bx += realPoints[pCnt].x;
			by += realPoints[pCnt].y;
		}
		return new RealPoint(bx / aantalPunten, by / aantalPunten);
	}

	public boolean isOnGrid(RealPoint rp)
	{	Point p = rp.toPoint();
		return ((p.x % owner.gridSize) == 0) && ((p.y % owner.gridSize) == 0);
	}


	// niet het dichtsbijzijnde grid-point maar een van de max 4
	// die nog binnen dit polygon ligt
	public void putRotationPointOnGrid(RealPoint rp)
	{	// punt al gevonden
		if (rotationPoint != null)
			return;
		// rp ligt al op grid	
		if (isOnGrid(rp))
		{	rotationPoint = rp;
			return;
		}
	
		Point aPoint = rp.toPoint();
		int gridX1 = aPoint.x / owner.gridSize;
		int gridX2 = gridX1 + 1;
		int gridY1 = aPoint.y / owner.gridSize;
		int gridY2 = gridY1 + 1;
		int gridDX = aPoint.x % owner.gridSize;
		int gridDY = aPoint.y % owner.gridSize;
		// maak 4 punten
		Vector unsortedGrPts = new Vector();
		unsortedGrPts.addElement(
			new RealPoint(gridX1 * owner.gridSize, gridY1 * owner.gridSize));
		unsortedGrPts.addElement(
			new RealPoint(gridX2 * owner.gridSize, gridY1 * owner.gridSize));
		unsortedGrPts.addElement(
			new RealPoint(gridX2 * owner.gridSize, gridY2 * owner.gridSize));
		unsortedGrPts.addElement(
			new RealPoint(gridX1 * owner.gridSize, gridY2 * owner.gridSize));			
		
		RealPoint[] gridPoints = new RealPoint[4];
		// sorteer op afstand tot rp
		for (int sCnt = 0; sCnt < 4; sCnt++)
		{	int elementsLeft = unsortedGrPts.size();
			RealPoint closest = (RealPoint) unsortedGrPts.elementAt(0);
			double closestDis = rp.distance(closest);
			for (int lCnt = 1; lCnt < elementsLeft; lCnt++)
			{	RealPoint grPoint = (RealPoint) unsortedGrPts.elementAt(lCnt);
				if (rp.distance(grPoint) < closestDis)
				{	closest = grPoint;
					closestDis = rp.distance(closest);
				}
			}
			gridPoints[sCnt] = closest;
			unsortedGrPts.removeElement(closest);
		}	
		
		// pak de eerste
		RealPoint gPoint = null;
		for (int gCnt = 0; gCnt < 4; gCnt++)
		{	boolean isOnEdge = (edgeContainsPoint(gridPoints[gCnt]) >= 0); 	
			if (!isOnEdge && contains(gridPoints[gCnt]) && (gPoint == null))
				gPoint = gridPoints[gCnt];
		}
		if (gPoint != null)
		{	rotationPoint = gPoint;
		}	
		else // lukt niet
		{	rotationPoint = rp;	
		}	
	}

	public RealPoint getRotationPoint()
	{	// rotationPoint al gevonden
		if (rotationPoint != null)
			return rotationPoint;
	
		// probeer altijd eerst het zwaartepunt
		RealPoint bc = getBaryCenter();
		boolean bcOnEdge = (edgeContainsPoint(bc) >= 0); 	
		if (!bcOnEdge && contains(bc))
		{	putRotationPointOnGrid(bc);
			// kijk of het gelukt is
			if (isOnGrid(rotationPoint))
				return rotationPoint;
		}		

		RealPoint insidePoint = null;
		double insideLength = 0;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	RealPoint aVertex = realPoints[pCnt];
			RealPoint aVertexPlusTwo = realPoints[(pCnt + 2) % aantalPunten];
			// edges die aVertex en aVertexPlusTwo NIET bevatten
			// zijn pCnt+3 t/m pCnt+3+(aantalPunten-5)=pCnt+(aantalPunten-2)
			// WEL zijn nl. pCnt+(aantalPunten-1), pCnt, pCnt+1, pCnt+2
//System.out.println("v = " + intPoints[pCnt].toString());			
//System.out.println("" + pCnt + " -> " + ((pCnt + 2) % aantalPunten));								
			boolean cuts = false;
			for (int qCnt = 3; qCnt < (aantalPunten - 1); qCnt++)
			{	RealPoint e1 = (RealPoint) realPoints[(pCnt + qCnt) % aantalPunten];
				RealPoint e2 = (RealPoint) realPoints[(pCnt + qCnt + 1) % aantalPunten];
				RealPoint isPoint = RealPoint.intersectSegments(
					aVertex, aVertexPlusTwo, e1, e2);
//if (isPoint != null)					
//System.out.println("cuts " + ((pCnt + qCnt) % aantalPunten) + 
//				   " -> " + ((pCnt + qCnt + 1) % aantalPunten));
//else
//System.out.println("!cuts " + ((pCnt + qCnt) % aantalPunten) + 
//				   " -> " + ((pCnt + qCnt + 1) % aantalPunten));
					
					
				cuts = cuts || (isPoint != null);
			}
			if (!cuts)
			{	
				RealPoint midPoint = new RealPoint(
					(aVertex.x + aVertexPlusTwo.x) / 2,
					(aVertex.y + aVertexPlusTwo.y) / 2);
				boolean isOnEdge = (edgeContainsPoint(midPoint) >= 0); 	
				Point midP = midPoint.toPoint();
				if (!isOnEdge && contains(midP))			
				{
					if (insidePoint == null)
					{	insidePoint = midPoint;
						insideLength = aVertex.distance(aVertexPlusTwo);
					}
					else
					{	double newInsideLength = aVertex.distance(aVertexPlusTwo);
						if (newInsideLength > (insideLength + RealPoint.NZero))
						{	insidePoint = midPoint;
							insideLength = newInsideLength;
						}
					}
				}						
			}
		
		}
		putRotationPointOnGrid(insidePoint);
		return rotationPoint;
		//return insidePoint;
	}

	// roteer dh graden om het rotatiepunt tegen de klok in
	public void rotate (double dh)
	{	RealPoint rp = getRotationPoint();
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	realPoints[pCnt] = realPoints[pCnt].rotate(dh, rp.x, rp.y);
			intPoints[pCnt] = realPoints[pCnt].toPoint();
		}
		maakIntPolygon();
	}
	
	public boolean contains(int x, int y)
	{	return intPolygon.contains(x, y);
	}

	public boolean contains(Point p)
	{	return intPolygon.contains(p.x, p.y);
	}
	
	public boolean contains(RealPoint rp)
	{	Point p = rp.toPoint();
		return contains(p);
	}
	
    public int containsVertex(RealPoint v)
    {   int index = -1;
        for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
        {   if (v.equals(realPoints[pCnt]))
               return pCnt;
        }
        return index;
    }
	
	// exact
	// index is die van beginpunt edge
	// point kan ook begin of einde edge zijn, i.e. een vertex
	public int edgeContainsPoint(RealPoint rp)
	{	int index = -1;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	RealPoint e1 = realPoints[pCnt];
			RealPoint e2 = realPoints[(pCnt + 1) % aantalPunten];
			boolean isOnEdge = rp.isOnSegment(e1, e2);
			if (isOnEdge)
			{	index = pCnt;
			}
		}
		return index;
	}

	// exact
	// index is die van beginpunt edge
	// point kan niet het begin of einde van de edge zijn, i.e. een geen vertex
	public int edgeStrictlyContainsPoint(RealPoint rp)
	{	int index = -1;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	RealPoint e1 = realPoints[pCnt];
			RealPoint e2 = realPoints[(pCnt + 1) % aantalPunten];
			boolean isStrictlyOnEdge = 
				rp.isOnSegment(e1, e2) && !rp.equals(e1) && !rp.equals(e2);
			if (isStrictlyOnEdge)
			{	index = pCnt;
			}
		}
		return index;
	}
	
	// voldoende dichtbij, geeft het dichtsbijzijnde punt op de 
	// edge terug
	// kan ook een van de uiteinden zijn
	public RealPoint edgeClicked(int clickX, int clickY)
	{	RealPoint result = null;
		RealPoint clickPoint = new RealPoint(clickX, clickY);
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	RealPoint e1 = realPoints[pCnt];
			RealPoint e2 = realPoints[(pCnt + 1) % aantalPunten];
			RealPoint closestPointOnLine = clickPoint.closestPointOnLine(e1, e2);
			if ((clickPoint.distance(closestPointOnLine) < owner.clickDis) &&
				closestPointOnLine.isOnSegment(e1, e2))
			{	result = closestPointOnLine;
			}
		}
		return result;
	}
	
	public RealPoint gridPointOnEdgeClicked(int clickX, int clickY)
	{	RealPoint result = null;
		RealPoint clickPoint = new RealPoint(clickX, clickY);
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	RealPoint e1 = realPoints[pCnt];
			RealPoint e2 = realPoints[(pCnt + 1) % aantalPunten];
			Vector gridPoints = gridPointsOnEdge(e1, e2, false);
			for (int gCnt = 0; gCnt < gridPoints.size(); gCnt++)
			{	RealPoint gPoint = (RealPoint) gridPoints.elementAt(gCnt);
				if (clickPoint.distance(gPoint) < owner.clickDis)
					result = gPoint;
			}
		}
		
		return result;
	}
	
	// alle gridpoints op de edge e1->e2 die NIET gelijk zijn aan e1 en e2
	public Vector gridPointsOnEdge(RealPoint e1, RealPoint e2, boolean includeEnds)
	{	Vector result = new Vector();
		Point e1Int = e1.toPoint();
		Point e2Int = e2.toPoint();
		int gridXStart = (e1Int.x / owner.gridSize) * owner.gridSize;
		int gridYStart = (e1Int.y / owner.gridSize) * owner.gridSize;
		int gridXEnd = (e2Int.x / owner.gridSize) * owner.gridSize;
		int gridYEnd = (e2Int.y / owner.gridSize) * owner.gridSize;
		if (gridXStart > gridXEnd)
		{	int temp = gridXStart;
			gridXStart = gridXEnd;
			gridXEnd = temp;
		}
		gridXEnd += owner.gridSize;
		if (gridYStart > gridYEnd)
		{	int temp = gridYStart;
			gridYStart = gridYEnd;
			gridYEnd = temp;
		}
		gridYEnd += owner.gridSize;
		for (int xCnt = gridXStart; xCnt <= gridXEnd; xCnt += owner.gridSize)
			for (int yCnt = gridYStart; yCnt <= gridYEnd; yCnt += owner.gridSize)
			{	RealPoint realPoint = new RealPoint(xCnt, yCnt);
				if (includeEnds)
				{
					if (realPoint.isOnSegment(e1, e2))
						result.addElement(realPoint);
				}
				else
				{	
					if (realPoint.isOnSegment(e1, e2) && 
							!realPoint.equals(e1) && !realPoint.equals(e2))
						result.addElement(realPoint);
				}	
			}
		
		
	
		return result;
	}
	
	public Rectangle getBoundingBox()
	{	int xMin = 10000;
		int xMax = 0;
		int yMin = 10000;
		int yMax = 0;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	xMin = Math.min(xMin, intPoints[pCnt].x);
			xMax = Math.max(xMax, intPoints[pCnt].x);
			yMin = Math.min(yMin, intPoints[pCnt].y);
			yMax = Math.max(yMax, intPoints[pCnt].y);			
		}		
		return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	// kijk of een of meerdere edges samenvallen met een zijde van de bounding box
	// return een vector met Points: x index van eerste edge point, y lengte
	public Vector getEdgesOnBoundingBox(int orientation)
	{	
		Rectangle bb = getBoundingBox();
		Vector bbEdges = new Vector();

		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	int e1x = intPoints[pCnt].x;
			int e1y = intPoints[pCnt].y;
			int e2x = intPoints[(pCnt + 1) % aantalPunten].x;
			int e2y = intPoints[(pCnt + 1) % aantalPunten].y;
			if (orientation == TOP)
			{	if ((e1y == bb.y) && (e2y == bb.y))
				{	bbEdges.addElement(new Point(pCnt, Math.abs(e1x - e2x)));
				}
			}
			else if (orientation == RIGHT)
			{	if ((e1x == (bb.x + bb.width)) && 
					(e2x == (bb.x + bb.width)))
				{	bbEdges.addElement(new Point(pCnt, Math.abs(e1y - e2y)));
				}	
			}
			else if (orientation == BOTTOM)
			{	if ((e1y == (bb.y + bb.height)) && 
					(e2y == (bb.y + bb.height)))
				{	bbEdges.addElement(new Point(pCnt, Math.abs(e1x - e2x)));
				}	
			}
			else if (orientation == LEFT)
			{	if ((e1x == bb.x) && (e2x == bb.x))
				{	bbEdges.addElement(new Point(pCnt, Math.abs(e1y - e2y)));
				}
			}
		}
		return bbEdges;
	}


	public Rectangle growBorder(Rectangle r, int bSize)
	{	return new Rectangle(r.x - bSize, r.y - bSize,
						     r.width + 2 * bSize, r.height + 2 * bSize);
	}


	// let op:
	// deze methode berekent niet alleen de positie van het balletje
	// dat het label tevoorschijn laat komen bij taaknummer 2,
	// maar ook de optimale positie van het label zelf;
	// i.h.b. moet je deze methode dus ook gebruiken bij taaknummer 3
	// ook al wordt daar het labelPoint niet getekend
	public void setLabelPoint()
	{	
		int offSet = 5;
	
		Vector topEdges = getEdgesOnBoundingBox(TOP);
		Vector rightEdges = getEdgesOnBoundingBox(RIGHT);
		Vector bottomEdges = getEdgesOnBoundingBox(BOTTOM);
		Vector leftEdges = getEdgesOnBoundingBox(LEFT);
		boolean found = false;
		RealPoint lPoint = null;
		Rectangle lRect = null;
		Rectangle iRect = null;
		// boven
		for (int eCnt = 0; eCnt < topEdges.size(); eCnt++)
		{	Point topPt = (Point) topEdges.elementAt(eCnt);
			int topIndex = topPt.x;
			int topLength = topPt.y;
			// probeer eerst linksboven
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[topIndex].x + owner.ovalSize, //owner.gridSize / 4,
					intPoints[topIndex].y + owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[topIndex].x, 
					intPoints[topIndex].y - offSet - labelHeight,
					labelWidth, labelHeight);	
				iRect = growBorder(lRect, offSet);	
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());		
				labelAlign = LEFT;		
//System.out.println("linksboven");				
			}
			// dan rechtsboven
			int topIndex2 = (topPt.x + 1) % aantalPunten;
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[topIndex2].x - owner.ovalSize, //owner.gridSize / 4,
					intPoints[topIndex2].y + owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[topIndex2].x - labelWidth, 
					intPoints[topIndex2].y - offSet - labelHeight,
					labelWidth, labelHeight);	
				iRect = growBorder(lRect, offSet);						
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());		
				labelAlign = RIGHT;
//System.out.println("rechtsboven");				
			}
			
				
		}
		// rechts
		for (int eCnt = 0; eCnt < rightEdges.size(); eCnt++)
		{	Point rightPt = (Point) rightEdges.elementAt(eCnt);
			int rightIndex = rightPt.x;
			int rightLength = rightPt.y;
			// eerst boven rechts
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[rightIndex].x - owner.ovalSize, //owner.gridSize / 4,
					intPoints[rightIndex].y + owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[rightIndex].x + offSet, 
					intPoints[rightIndex].y,
					labelWidth, labelHeight);		
				iRect = growBorder(lRect, offSet);							
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());					
				labelAlign = LEFT;
//System.out.println("boven rechts");				
			}
			// dan onder rechts
			int rightIndex2 = (rightPt.x + 1) % aantalPunten;			
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[rightIndex2].x - owner.ovalSize, //owner.gridSize / 4,
					intPoints[rightIndex2].y - owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[rightIndex2].x + offSet, 
					intPoints[rightIndex2].y - labelHeight,
					labelWidth, labelHeight);		
				iRect = growBorder(lRect, offSet);							
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());					
				labelAlign = LEFT;
//System.out.println("onder rechts");				
			}
			
		}		
		for (int eCnt = 0; eCnt < bottomEdges.size(); eCnt++)
		{	Point bottomPt = (Point) bottomEdges.elementAt(eCnt);
			int bottomIndex = bottomPt.x;
			int bottomLength = bottomPt.y;
			// eerst rechtsonder
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[bottomIndex].x - owner.ovalSize, //owner.gridSize / 4,
					intPoints[bottomIndex].y - owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[bottomIndex].x - labelWidth, 
					intPoints[bottomIndex].y + offSet,
					labelWidth, labelHeight);
				iRect = growBorder(lRect, offSet);										
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());						
				labelAlign = RIGHT;
//System.out.println("rechtsonder");				
			}
			// dan linksonder
			int bottomIndex2 = (bottomPt.x + 1) % aantalPunten;
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[bottomIndex2].x + owner.ovalSize, //owner.gridSize / 4,
					intPoints[bottomIndex2].y - owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[bottomIndex2].x, 
					intPoints[bottomIndex2].y + offSet,
					labelWidth, labelHeight);
				iRect = growBorder(lRect, offSet);										
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());						
				labelAlign = LEFT;
//System.out.println("linksonder");				
			}

		}		
		for (int eCnt = 0; eCnt < leftEdges.size(); eCnt++)
		{	Point leftPt = (Point) leftEdges.elementAt(eCnt);
			int leftIndex = leftPt.x;
			int leftLength = leftPt.y;
			// eerst onder links
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[leftIndex].x + owner.ovalSize,//owner.gridSize / 4,
					intPoints[leftIndex].y - owner.ovalSize);//owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[leftIndex].x - offSet - labelWidth, 
					intPoints[leftIndex].y - labelHeight,
					labelWidth, labelHeight);
				iRect = growBorder(lRect, offSet);													
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());							
				labelAlign = RIGHT;
//System.out.println("onder links");				
			}
			// dan boven links
			int leftIndex2 = (leftPt.x + 1) % aantalPunten;			
			if (!found)
			{	lPoint = new RealPoint(
					intPoints[leftIndex].x + owner.ovalSize, //owner.gridSize / 4,
					intPoints[leftIndex].y + owner.ovalSize); //owner.gridSize / 4);
				lRect = new Rectangle(
					intPoints[leftIndex].x - offSet - labelWidth, 
					intPoints[leftIndex].y,
					labelWidth, labelHeight);		
				iRect = growBorder(lRect, offSet);											
				found = !owner.knipPolygonIntersects(iRect, this);// &&
						//intPolygon.contains(lPoint.toPoint());							
				labelAlign = RIGHT;
//System.out.println("boven links");				
			}
		
		}	
		if (found)
		{	labelPoint = lPoint;
			labelRect = lRect;
		}	
		else
		{	labelPoint = null;
			labelRect = null;
//System.out.println("geen");			
		}
	}
	

	
}

/*
class ScormPolygon implements Serializable
{
	Vector realPoints = new Vector(0);
	int oppervlakte;
	
	
	public ScormPolygon(RealPoint[] rPoints)
	{	
		for (int pCnt = 0; pCnt < rPoints.length; pCnt++)
		{	realPoints.addElement(new RealPoint(rPoints[pCnt]));
		}
	}
	
}

*/