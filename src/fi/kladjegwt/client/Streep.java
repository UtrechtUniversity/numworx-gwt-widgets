package fi.kladjegwt.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Streep 
{
	CssColor kleur;
	double[] puntenXD, puntenYD;
	double[] pXD, pYD;
	int bbFactor = 4;
	Polygon bb;
	double cx = 0, cy = 0;
	AffineTransform at = new AffineTransform();
	//double m00 = 1;
	//double m01 = 0;
	//double m10 = 0;
	//double m11 = 1;
	double rotation = 0;

	Rectangle handleBox;
	int hbFactor = 4;
	int breedte, hoogte;
	
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;

	public Streep(CssColor c, ArrayList<DoublePoint> punten)
	{	kleur = c;
		puntenXD = new double[punten.size()];
		puntenYD = new double[punten.size()];

		for (int pCnt = 0; pCnt < punten.size(); pCnt++)
		{	DoublePoint pt = punten.get(pCnt);
			puntenXD[pCnt] = pt.x;
			puntenYD[pCnt] = pt.y;
			
			cx += puntenXD[pCnt];
			cy += puntenYD[pCnt];
		}
		
		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
	}
	
	public Streep(CssColor c, Vector<Point> punten)
	{	kleur = c;
		puntenXD = new double[punten.size()];
		puntenYD = new double[punten.size()];

		for (int pCnt = 0; pCnt < punten.size(); pCnt++)
		{	Point pt = (Point) punten.elementAt(pCnt);
			puntenXD[pCnt] = pt.x;
			puntenYD[pCnt] = pt.y;
		
			cx += puntenXD[pCnt];
			cy += puntenYD[pCnt];
		}
		
		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
	}
	
	public Streep(CssColor c, double[] ptXD, double[] ptYD)
	{	kleur = c;
	
		puntenXD = new double[ptXD.length];
		puntenYD = new double[ptYD.length];

		for (int cnt = 0; cnt < ptXD.length; cnt++) 
		{	puntenXD[cnt] = ptXD[cnt];
			puntenYD[cnt] = ptYD[cnt];
			
			cx += puntenXD[cnt];
			cy += puntenYD[cnt];
			
		}

		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();

	}

	public Streep(CssColor c, int[] ptX, int[] ptY)
	{	kleur = c;
		puntenXD = new double[ptX.length];
		puntenYD = new double[ptY.length];

		for (int cnt = 0; cnt < ptX.length; cnt++) 
		{	puntenXD[cnt] = ptX[cnt];
			puntenYD[cnt] = ptY[cnt];
			
			cx += puntenXD[cnt];
			cy += puntenYD[cnt];

		}

		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
		//maakBBs();
		//makeHandleBox();
	}
	
	public void maakStreep()
	{
		pXD = new double[puntenXD.length];
		pYD = new double[puntenYD.length];
		double minX = 1000;
		double maxX = -100;
		double minY = 1000;
		double maxY = -100;
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{
			pXD[pCnt] = puntenXD[pCnt];
			pYD[pCnt] = puntenYD[pCnt];
			
			if (puntenXD[pCnt] < minX)
				minX = puntenXD[pCnt];
			if (puntenXD[pCnt] > maxX)
				maxX = puntenXD[pCnt];
			if (puntenYD[pCnt] < minY)
				minY = puntenYD[pCnt];
			if (puntenYD[pCnt] > maxY)
				maxY = puntenYD[pCnt];
		}
		breedte = (int) Math.round(maxX - minX);
		hoogte = (int) Math.round(maxY - minY);
		
		maakBBs();
		
		rotateStreep(rotation);
		
		bb.rotate(rotation, cx, cy);
		
		makeHandleBox();
	}
	
	
	public void makeHandleBox()
	{
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			if (bb.puntenX[pCnt] < minX)
				minX = bb.puntenX[pCnt];
			if (bb.puntenX[pCnt] > maxX)
				maxX = bb.puntenX[pCnt];
			if (bb.puntenY[pCnt] < minY)
				minY = bb.puntenY[pCnt];
			if (bb.puntenY[pCnt] > maxY)
				maxY = bb.puntenY[pCnt];
		}
		
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);
		
		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		if (KladjeGWTVeld.roteren)
			makeRotateHandles();
		
	}
	
	public void makeScaleHandles()
	{
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}
	
	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}

	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width,// - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor, 
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
	}
	
	
	public void maakBBs()
	{
		bb = new Polygon();
		
		if (puntenXD.length == 1)
		{	bb.addPoint((int) Math.round(puntenXD[0] - bbFactor), (int) Math.round(puntenYD[0] - bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] + bbFactor), (int) Math.round(puntenYD[0] - bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] + bbFactor), (int) Math.round(puntenYD[0] + bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] - bbFactor), (int) Math.round(puntenYD[0] + bbFactor));
		}
		if (puntenXD.length > 1)
		{  
			
			// for loop 1
			for (int pCnt = 1; pCnt < puntenXD.length; pCnt++)
			{	
				double fromX = puntenXD[pCnt - 1];
				double fromY = puntenYD[pCnt - 1];
				double toX = puntenXD[pCnt];
				double toY = puntenYD[pCnt];
				// richtingsvector
				double rX = toX - fromX;
				double rY = toY - fromY;
				// normaalvector
				double nx = 0;
				double ny = 0;
				boolean normal1 = true;
				if (Math.abs(rX) > Math.abs(rY))
				{	nx = rY;
					ny = -rX;
					normal1 = false;
				}
				else
				{	nx = -rY;
					ny = rX;
				}
				// eenheids normaalvector
				double nl = Math.sqrt(nx * nx + ny * ny);
				if (nl > 0)
				{	 
					if (normal1)
					{	
						// eerste punt
						double px = fromX + nx * bbFactor / nl;
						double py = fromY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py)); 
						// 	tweede punt
						px = toX + nx * bbFactor / nl;
						py = toY + ny * bbFactor / nl;
						if (normal1)
							bb.addPoint((int) Math.round(px), (int) Math.round(py));
						}
					else
					{	
//					 	vierde punt
						double px = fromX - nx * bbFactor / nl;
						double py = fromY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
						
						//derde punt
						px = toX - nx * bbFactor / nl;
						py = toY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));

						// 	vierde punt
						px = fromX - nx * bbFactor / nl;
						py = fromY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}	
				}	
		
			}
		
			// for loop 2
			for (int pCnt = puntenXD.length - 1; pCnt > 0; pCnt--)
			{	
				double fromX = puntenXD[pCnt - 1];
				double fromY = puntenYD[pCnt - 1];
				double toX = puntenXD[pCnt];
				double toY = puntenYD[pCnt];
				// richtingsvector
				double rX = toX - fromX;
				double rY = toY - fromY;
				// normaalvector
				double nx = 0;
				double ny = 0;
				boolean normal1 = true;
				if (Math.abs(rX) > Math.abs(rY))
				{	nx = rY;
					ny = -rX;
					normal1 = false;
				}
				else
				{	nx = -rY;
					ny = rX;
				}
				// eenheids normaalvector
				double nl = Math.sqrt(nx * nx + ny * ny);
				if (nl > 0)
				{	
					if (!normal1)
					{	
						// 	tweede punt
						double px = toX + nx * bbFactor / nl;
						double py = toY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					
						// eerste punt
						px = fromX + nx * bbFactor / nl;
						py = fromY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}
					else
					{	
						
						// derde punt
						double px = toX - nx * bbFactor / nl;
						double py = toY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
						// 	vierde punt
						px = fromX - nx * bbFactor / nl;
						py = fromY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}
				}	
		
			}
		}
		
	}
	public void rotate(double rotateStep)
	{	rotation += rotateStep;
	
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = Math.cos(rotateStep) * (pXD[pCnt] - cx) - Math.sin(rotateStep) * (pYD[pCnt] - cy);
			double pYDNew = Math.sin(rotateStep) * (pXD[pCnt] - cx) + Math.cos(rotateStep) * (pYD[pCnt] - cy);
			pXD[pCnt] = pXDNew + cx;
			pYD[pCnt] = pYDNew + cy;
		}
		
		//double m00New  = Math.cos(rotateStep) * m00 - Math.sin(rotateStep) * m10;
		//double m10New  = Math.sin(rotateStep) * m00 + Math.cos(rotateStep) * m10;
		//double m01New = Math.cos(rotateStep) * m01 - Math.sin(rotateStep) * m11;
		//double m11New = Math.sin(rotateStep) * m01 + Math.cos(rotateStep) * m11;
		
		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);

		bb.rotate(rotateStep, cx, cy);
	
		makeHandleBox();
		
		
		
	}

	public void rotate(double rotateStep, double dx, double dy)
	{	
	
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = Math.cos(rotateStep) * (pXD[pCnt] - dx) - Math.sin(rotateStep) * (pYD[pCnt] - dy);
			double pYDNew = Math.sin(rotateStep) * (pXD[pCnt] - dx) + Math.cos(rotateStep) * (pYD[pCnt] - dy);
			pXD[pCnt] = pXDNew + dx;
			pYD[pCnt] = pYDNew + dy;
		}

		
		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
												  Math.sin(rotateStep), Math.cos(rotateStep), 
												  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
												  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);
		
		bb.rotate(rotateStep, dx, dy);
	
		makeHandleBox();
	}
	
	public void rotateStreep(double rotation)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = Math.cos(rotation) * (pXD[pCnt] - cx) - Math.sin(rotation) * (pYD[pCnt] - cy);
			double pYDNew = Math.sin(rotation) * (pXD[pCnt] - cx) + Math.cos(rotation) * (pYD[pCnt] - cy);
			pXD[pCnt] = pXDNew + cx;
			pYD[pCnt] = pYDNew + cy;
		}
		
		//double m00New  = Math.cos(rotation) * m00 - Math.sin(rotation) * m10;
		//double m10New  = Math.sin(rotation) * m00 + Math.cos(rotation) * m10;
		//double m01New = Math.cos(rotation) * m01 - Math.sin(rotation) * m11;
		//double m11New = Math.sin(rotation) * m01 + Math.cos(rotation) * m11;
		
		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotation),- Math.sin(rotation),
				  Math.sin(rotation), Math.cos(rotation), 
				  cx - Math.cos(rotation) * cx + Math.sin(rotation) * cy, 
				  cy - Math.sin(rotation) * cx - Math.cos(rotation) * cy);

		at = at.leftMultiplyBy(rot);
		
	}
	
	public void scale(double scaleStep)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			puntenXD[pCnt] = scaleStep * puntenXD[pCnt] + (1 - scaleStep) * cx;
			puntenYD[pCnt] = scaleStep * puntenYD[pCnt] + (1 - scaleStep) * cy;
		}
		
		//m00 *= scaleStep;
		//m10 *= scaleStep;
		//m01 *= scaleStep;
		//m11 *= scaleStep;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);

		bb.scale(scaleStep, cx, cy);
		
		//maakStreep();
		
		makeHandleBox();
		
	}
	
	public void scale(double scaleStep, double dx, double dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStep * pXD[pCnt] + (1 - scaleStep) * dx;
			pYD[pCnt] = scaleStep * pYD[pCnt] + (1 - scaleStep) * dy;
		}
		
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);
		
		bb.scale(scaleStep, dx, dy);

		makeHandleBox();
		
	}


	public void scale(double scaleStepX, double scaleStepY)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			puntenXD[pCnt] = scaleStepX * puntenXD[pCnt] + (1 - scaleStepX) * cx;
			puntenYD[pCnt] = scaleStepY * puntenYD[pCnt] + (1 - scaleStepY) * cy;
		}
		
		//m00 *= scaleStepX;
		//m10 *= scaleStepY;
		//m01 *= scaleStepX;
		//m11 *= scaleStepY;
		
		AffineTransform sc = new AffineTransform(scaleStepX, 0, 0, scaleStepY, (1 - scaleStepX) * cx, (1 - scaleStepY) * cy);
		at = at.leftMultiplyBy(sc);

		
		bb.scale(scaleStepX, scaleStepY, cx, cy);
		
		//maakStreep();
		
		makeHandleBox();
		
	}
	
	public void scale(double scaleStepX, double scaleStepY, double dx, double dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStepX * pXD[pCnt] + (1 - scaleStepX) * dx;
			pYD[pCnt] = scaleStepY * pYD[pCnt] + (1 - scaleStepY) * dy;
		}
		
		cx = scaleStepX * cx + (1 - scaleStepX) * dx;
		cy = scaleStepY * cy + (1 - scaleStepY) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStepX, 0, 0, scaleStepY, (1 - scaleStepX) * dx, (1 - scaleStepY) * dy);
		at = at.leftMultiplyBy(sc);
		
		
		bb.scale(scaleStepX, scaleStepY, dx, dy);
		
		
		makeHandleBox();
		
	}
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		
		ArrayList<Double> puntenXDAL = new ArrayList<Double>();
		ArrayList<Double> puntenYDAL = new ArrayList<Double>();
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{
			puntenXDAL.add(new Double(puntenXD[pCnt]));
			puntenYDAL.add(new Double(puntenYD[pCnt]));
		}

		h.put("puntenXD", puntenXDAL);
		h.put("puntenYD", puntenYDAL);
		
//		h.put("rotation", new Double(rotation));
		
		h.put("m00", new Double(at.m00));
		h.put("m10", new Double(at.m10));
		h.put("m01", new Double(at.m01));
		h.put("m11", new Double(at.m11));
		h.put("b0", new Double(at.b0));
		h.put("b1", new Double(at.b1));


	
		return h;
	}
	
	public static Streep setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		double[] puntenXD = new double[0];
		double[] puntenYD = new double[0];
		List<Double> puntenXDAL = new ArrayList<Double>();
		List<Double> puntenYDAL = new ArrayList<Double>();
		double rotation = 0;
		
		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		
		// data from getState
		//if (h.containsKey("puntenXAL"))
		//	puntenXAL = (ArrayList<Integer>) h.get("puntenXAL");
		//if (h.containsKey("puntenYAL"))
		//	puntenYAL = (ArrayList<Integer>) h.get("puntenYAL");
		
		// launchdata or data from getState
		if (h.containsKey("puntenXD"))
			puntenXDAL = h.getDoubleList("puntenXD");
		if (h.containsKey("puntenYD"))
			puntenYDAL = h.getDoubleList("puntenYD");

		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");

		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");

		
		puntenXD = new double[puntenXDAL.size()];
		puntenYD = new double[puntenYDAL.size()];
		for (int pCnt = 0; pCnt < puntenXDAL.size(); pCnt++)
		{
			puntenXD[pCnt] = ((Number) puntenXDAL.get(pCnt)).doubleValue();
			puntenYD[pCnt] = ((Number) puntenYDAL.get(pCnt)).doubleValue();
		}
		
		
		Streep streep = new Streep(kleur, puntenXD, puntenYD);
		streep.rotation = rotation;
		streep.maakStreep();
		
		//if (h.containsKey("rotation"))
		//{	streep.rotate(rotation);
		//}
		//else 
		if (h.containsKey("b0"))
		{	streep.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else
		{	streep.transformBy(m00, m01, m10, m11);
		}
		
		return streep;
	}

	public void transformBy(double m00, double m01, double m10, double m11)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);
		
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = at.m00 * pXD[pCnt] + at.m01 * pYD[pCnt] + at.b0;
			double pYDNew = at.m10 * pXD[pCnt] + at.m11 * pYD[pCnt] + at.b1;
			pXD[pCnt] = pXDNew;
			pYD[pCnt] = pYDNew;
			
		}
		
		bb.transformBy(at);
		
		makeHandleBox();
	}

	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = at.m00 * pXD[pCnt] + at.m01 * pYD[pCnt] + at.b0;
			double pYDNew = at.m10 * pXD[pCnt] + at.m11 * pYD[pCnt] + at.b1;
			pXD[pCnt] = pXDNew;
			pYD[pCnt] = pYDNew;
			
		}
		
		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		bb.transformBy(at);
		
		makeHandleBox();
	}
	
	public void teken(Context2d g)
	{
		
		g.setStrokeStyle(kleur);		
		
		if (puntenXD.length == 1)
		{	g.beginPath();
			g.strokeRect(pXD[0], pYD[0], 1, 1);
		}
		if (puntenXD.length > 1)
		{	
			g.beginPath();
			g.moveTo(pXD[0], pYD[0]);
			for (int pCnt = 1; pCnt < puntenXD.length; pCnt++)
			{	g.lineTo(pXD[pCnt], pYD[pCnt]);
			}
			g.stroke();
			
		}
		
	}
	
	public void tekenHandleBox(Context2d g)
	{

		//float[] dash = new float[2];
		//dash[0] = 2;
		//dash[1] = 2;
		//g.setStroke(new BasicStroke(1.0f, 2, 0, 10.0f, dash, 0.0f));
		
		//g.setColor(KladjeVeld.hbColor);

		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		//g.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topRightHandle);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topLeftHandle);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomRightHandle);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomLeftHandle);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x + handleBox.width, // - 2 * hbFactor,
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
		
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x - 4 * hbFactor, 
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
			
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}

	}
	
	public void tekenBB(Context2d g)
	{
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.moveTo(bb.doubleX[0], bb.doubleY[0]);
		for (int k = 1; k < bb.aantalPunten; k++) 
		{	g.lineTo(bb.doubleX[k], bb.doubleY[k]);
		}
		g.lineTo(bb.doubleX[0], bb.doubleY[0]);
		g.closePath();
		g.stroke();

			
		g.setLineWidth(1.5d);
	}

/*	
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
*/
/*	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
*/	
	public boolean bbContains(int x, int y)
	{

		return bb.contains(x, y);
	}
	
	public void translate(int dx, int dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	//puntenXD[pCnt] += dx;
			//puntenYD[pCnt] += dy;
			pXD[pCnt] += dx;
			pYD[pCnt] += dy; 
		}

		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);
		
		cx += dx;
		cy += dy;

		bb.translate(dx, dy);
		
		//bb2.translate(dx, dy);
		makeHandleBox();
	}
	
	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;		
		
		boolean isContainedIn = true;
		
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			isContainedIn = isContainedIn && r.contains(bb.puntenX[pCnt], bb.puntenY[pCnt]);

		}
		
		return isContainedIn;
	}
}
class Lijn
{
	CssColor kleur;
	int fromX, fromY, toX, toY;
	double fX, fY, tX, tY;
	int bbFactor = 4;
	//Polygon lijn; 
	Polygon bb;
	//Polygon lijnBB;
	double cx, cy;
	AffineTransform at = new AffineTransform();
	
	double rotation = 0;
	
	//double m00 = 1;
	//double m01 = 0;
	//double m10 = 0;
	//double m11 = 1;
	
	Rectangle handleBox;
	int hbFactor = 4;
	
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;
	
	public Lijn(CssColor c, int fromX, int fromY, int toX, int toY)
	{
		kleur = c;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;

		cx = ((double) fromX + (double) toX) / 2;
		cy = ((double) fromY + (double) toY) / 2;
		
		//makeBB();
		maakLijn();
	}
	
	public void maakLijn()
	{
		fX = fromX;
		fY = fromY;
		tX = toX;
		tY = toY;
				
		makeBB();
		
		rotateLijn(rotation);
		
		bb.rotate(rotation, cx, cy);
		
		makeHandleBox();
	}
	
/*	
	
	public void makeLijn()
	{
		lijn = new Polygon();
		lijn.addPoint(fromX, fromY);
		lijn.addPoint(toX, toY);
	}
*/
/*	
	public void makeLijnBB()
	{
		lijnBB = new Polygon(bb);
	}
*/

	public void makeHandleBox()
	{
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			if (bb.puntenX[pCnt] < minX)
				minX = bb.puntenX[pCnt];
			if (bb.puntenX[pCnt] > maxX)
				maxX = bb.puntenX[pCnt];
			if (bb.puntenY[pCnt] < minY)
				minY = bb.puntenY[pCnt];
			if (bb.puntenY[pCnt] > maxY)
				maxY = bb.puntenY[pCnt];
		}
		
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);

		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		if (KladjeGWTVeld.roteren)
			makeRotateHandles();
		
	}
	
	public void makeScaleHandles()
	{
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}

	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}
	
	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width,// - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor, 
				                         handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
		
	}
	
	public void makeBB()
	{
		bb = new Polygon();
		
		// richtingsvector
		double rX = toX - fromX;
		double rY = toY - fromY;
		// normaalvector
		double nx = 0;
		double ny = 0;
		if (Math.abs(rX) > Math.abs(rY))
		{	nx = rY;
			ny = -rX;
		}
		else
		{	nx = -rY;
			ny = rX;
		}
		// eenheids normaalvector
		double nl = Math.sqrt(nx * nx + ny * ny);
		if (nl > 0)
		{	// eerste punt
			double px = fromX + nx * bbFactor / nl;
			double py = fromY + ny * bbFactor / nl;
			bb.addPoint(px, py); 
			// tweede punt
			px = toX + nx * bbFactor / nl;
			py = toY + ny * bbFactor / nl;
			bb.addPoint(px, py);
			// derde punt
			px = toX - nx * bbFactor / nl;
			py = toY - ny * bbFactor / nl;
			bb.addPoint(px, py);
			// vierde punt
			px = fromX - nx * bbFactor / nl;
			py = fromY - ny * bbFactor / nl;
			bb.addPoint(px, py);
		
		
		}
		else
		{
			bb.addPoint(fromX - bbFactor, fromY - bbFactor);
			bb.addPoint(fromX + bbFactor, fromY - bbFactor);
			bb.addPoint(fromX + bbFactor, fromY + bbFactor);
			bb.addPoint(fromX - bbFactor, fromY + bbFactor);
		}
	}

	public void rotate(double rotateStep)
	{	rotation += rotateStep;
	
		double fXNew = Math.cos(rotateStep) * (fX - cx) - Math.sin(rotateStep) * (fY - cy);
		double fYNew = Math.sin(rotateStep) * (fX - cx) + Math.cos(rotateStep) * (fY - cy);
		double tXNew = Math.cos(rotateStep) * (tX - cx) - Math.sin(rotateStep) * (tY - cy);
		double tYNew = Math.sin(rotateStep) * (tX - cx) + Math.cos(rotateStep) * (tY - cy);
		fX = fXNew + cx;
		fY = fYNew + cy;
		tX = tXNew + cx;
		tY = tYNew + cy;
	
		//double m00New  = Math.cos(rotateStep) * m00 - Math.sin(rotateStep) * m10;
		//double m10New  = Math.sin(rotateStep) * m00 + Math.cos(rotateStep) * m10;
		//double m01New = Math.cos(rotateStep) * m01 - Math.sin(rotateStep) * m11;
		//double m11New = Math.sin(rotateStep) * m01 + Math.cos(rotateStep) * m11;
		
		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);

		bb.rotate(rotateStep, cx, cy);
		
		//makeBB();
		//bb2 = rotatePolygon(bb2, rotation, cx, cy);

		makeHandleBox();
	
	}

	public void rotate(double rotateStep, double dx, double dy)
	{	//rotation += rotateStep;
	
		double fXNew = Math.cos(rotateStep) * (fX - dx) - Math.sin(rotateStep) * (fY - dy);
		double fYNew = Math.sin(rotateStep) * (fX - dx) + Math.cos(rotateStep) * (fY - dy);
		double tXNew = Math.cos(rotateStep) * (tX - dx) - Math.sin(rotateStep) * (tY - dy);
		double tYNew = Math.sin(rotateStep) * (tX - dx) + Math.cos(rotateStep) * (tY - dy);
		fX = fXNew + dx;
		fY = fYNew + dy;
		tX = tXNew + dx;
		tY = tYNew + dy;
		
		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  dx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * dy, 
				  dy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);

		bb.rotate(rotateStep, dx, dy);
		
		makeHandleBox();
	
	}

	public void rotateLijn(double rotation)
	{		
		double fXNew = Math.cos(rotation) * (fX - cx) - Math.sin(rotation) * (fY - cy);
		double fYNew = Math.sin(rotation) * (fX - cx) + Math.cos(rotation) * (fY - cy);
		double tXNew = Math.cos(rotation) * (tX - cx) - Math.sin(rotation) * (tY - cy);
		double tYNew = Math.sin(rotation) * (tX - cx) + Math.cos(rotation) * (tY - cy);
		fX = fXNew + cx;
		fY = fYNew + cy;
		tX = tXNew + cx;
		tY = tYNew + cy;
		
		//double m00New  = Math.cos(rotation) * m00 - Math.sin(rotation) * m10;
		//double m10New  = Math.sin(rotation) * m00 + Math.cos(rotation) * m10;
		//double m01New = Math.cos(rotation) * m01 - Math.sin(rotation) * m11;
		//double m11New = Math.sin(rotation) * m01 + Math.cos(rotation) * m11;
		
		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotation),- Math.sin(rotation),
				  Math.sin(rotation), Math.cos(rotation), 
				  cx - Math.cos(rotation) * cx + Math.sin(rotation) * cy, 
				  cy - Math.sin(rotation) * cx - Math.cos(rotation) * cy);

		at = at.leftMultiplyBy(rot);


		
	}	

	public void scale(double scaleStep)
	{	
//		fromX = (int) Math.round(scaleStep * fromX + (1 - scaleStep) * cx);
//		fromY = (int) Math.round(scaleStep * fromY + (1 - scaleStep) * cy);
//		toX = (int) Math.round(scaleStep * toX + (1 - scaleStep) * cx);
//		toY = (int) Math.round(scaleStep * toY + (1 - scaleStep) * cy);
		
		fX = scaleStep * fX + (1 - scaleStep) * cx;
		fY = scaleStep * fY + (1 - scaleStep) * cy;
		tX = scaleStep * tX + (1 - scaleStep) * cx;
		tY = scaleStep * tY + (1 - scaleStep) * cy;
		
		//m00 *= scaleStep;
		//m10 *= scaleStep;
		//m01 *= scaleStep;
		//m11 *= scaleStep;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);

		
		bb.scale(scaleStep, scaleStep, cx, cy);
		
		makeHandleBox();
		
	}

	public void scale(double scaleStep, double dx, double dy)
	{	
		fX = scaleStep * fX + (1 - scaleStep) * dx;
		fY = scaleStep * fY + (1 - scaleStep) * dy;
		tX = scaleStep * tX + (1 - scaleStep) * dx;
		tY = scaleStep * tY + (1 - scaleStep) * dy;
		
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);
		
		bb.scale(scaleStep, scaleStep, dx, dy);

		
		makeHandleBox();
		
	}

	public void scale(double sx, double sy)
	{	
//		fromX = (int) Math.round(sx * fromX + (1 - sx) * cx);
//		fromY = (int) Math.round(sy * fromY + (1 - sy) * cy);
//		toX = (int) Math.round(sx * toX + (1 - sx) * cx);
//		toY = (int) Math.round(sy * toY + (1 - sy) * cy);
		
		fX = sx * fX + (1 - sx) * cx;
		fY = sy * fY + (1 - sy) * cy;
		tX = sx * tX + (1 - sx) * cx;
		tY = sy * tY + (1 - sy) * cy;
		
		
		//m00 *= sx;
		//m10 *= sy;
		//m01 *= sx;
		//m11 *= sy;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);

		
		bb.scale(sx, sy, cx, cy);
		
		//maakLijn();
		makeHandleBox();
		
	}
	
	public void scale(double sx, double sy, double dx, double dy)
	{	
		fX = sx * fX + (1 - sx) * dx;
		fY = sy * fY + (1 - sy) * dy;
		tX = sx * tX + (1 - sx) * dx;
		tY = sy * tY + (1 - sy) * dy;
		
		cx = sx * cx + (1 - sx) * dx;
		cy = sy * cy + (1 - sy) * dy;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);

		bb.scale(sx, sy, dx, dy);
		
		makeHandleBox();
		
	}

	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("fromX", new Integer(fromX));
		h.put("fromY", new Integer(fromY));
		h.put("toX", new Integer(toX));
		h.put("toY", new Integer(toY));

//		h.put("rotation", new Double(rotation));

		h.put("m00", new Double(at.m00));
		h.put("m10", new Double(at.m10));
		h.put("m01", new Double(at.m01));
		h.put("m11", new Double(at.m11));
		h.put("b0", new Double(at.b0));
		h.put("b1", new Double(at.b1));

		
		return h;
	}
	
	public static Lijn setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		int fromX = 0; 
		int fromY = 0;
		int toX = 0;
		int toY = 0;
		double rotation = 0;

		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("fromX"))
			fromX = h.getInt("fromX");
		if (h.containsKey("fromY"))
			fromY = h.getInt("fromY");

		if (h.containsKey("toX"))
			toX = h.getInt("toX");
		if (h.containsKey("toY"))
			toY = h.getInt("toY");

		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");

		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");

		
		Lijn lijn = new Lijn(kleur, fromX, fromY, toX, toY);
		lijn.rotation = rotation;
		lijn.maakLijn();

		//if (h.containsKey("rotation"))
		//{	lijn.rotate(rotation);
		//}
		//else 
		if (h.containsKey("b00"))
		{	lijn.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else
		{	lijn.transformBy(m00, m01, m10, m11);
		}
		
		return lijn;
	}

	public void transformBy(double m00, double m01, double m10, double m11)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);

		double fXNew = at.m00 * fX + at.m01 * fY + at.b0;
		double fYNew = at.m10 * fX + at.m11 * fY + at.b1;
		fX = fXNew;
		fY = fYNew;
		
		double tXNew = at.m00 * tX + at.m01 * tY + at.b0;
		double tYNew = at.m10 * tX + at.m11 * tY + at.b1;
		tX = tXNew;
		tY = tYNew;

		bb.transformBy(at);
		
		makeHandleBox();
		
		
	}
	
	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);

		double fXNew = at.m00 * fX + at.m01 * fY + at.b0;
		double fYNew = at.m10 * fX + at.m11 * fY + at.b1;
		fX = fXNew;
		fY = fYNew;
		
		double tXNew = at.m00 * tX + at.m01 * tY + at.b0;
		double tYNew = at.m10 * tX + at.m11 * tY + at.b1;
		tX = tXNew;
		tY = tYNew;

		bb.transformBy(at);

		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		makeHandleBox();
		
		
	}
	
	public void teken(Context2d g)
	{
		//makeLijn();
		//lijn.rotate(rotation, cx, cy);
		
		g.setStrokeStyle(kleur);
		
		g.beginPath();
		//g.moveTo(lijn.doubleX[0], lijn.doubleY[0]);
		//g.lineTo(lijn.doubleX[1], lijn.doubleY[1]);
		g.moveTo(fX, fY);
		g.lineTo(tX, tY);
		g.stroke();

	}

	public void tekenHandleBox(Context2d g)
	{

		//float[] dash = new float[2];
		//dash[0] = 2;
		//dash[1] = 2;
		//g.setStroke(new BasicStroke(1.0f, 2, 0, 10.0f, dash, 0.0f));
		
		//g.setColor(KladjeVeld.hbColor);

		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		//g.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topRightHandle);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topLeftHandle);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomRightHandle);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomLeftHandle);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x + handleBox.width, // - 2 * hbFactor,
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
		
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x - 4 * hbFactor, 
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
			
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}

	}
	
	public void tekenBB(Context2d g)
	{
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.moveTo(bb.doubleX[0], bb.doubleY[0]);
		for (int k = 1; k < bb.aantalPunten; k++) 
		{	g.lineTo(bb.doubleX[k], bb.doubleY[k]);
		}
		g.lineTo(bb.doubleX[0], bb.doubleY[0]);
		g.closePath();
		g.stroke();

			
		g.setLineWidth(1.5d);
	}
/*
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
*/
/*	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
*/	
	public boolean bbContains(int x, int y)
	{
		//int rx = inverseTransformX(x, y);
		//int ry = inverseTransformY(x, y);
		
		return bb.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		//fromX += dx;
		//fromY += dy;
		toX += dx; 
		toY += dy;
		cx += dx;
		cy += dy;
		
		fX += dx;
		fY += dy;
		tX += dx; 
		tY += dy;

		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		
		bb.translate(dx, dy);

		makeHandleBox();

		
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;
		
		boolean isContainedIn = true;
		
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			isContainedIn = isContainedIn && r.contains(bb.puntenX[pCnt], bb.puntenY[pCnt]);		}
		
		return isContainedIn;
	}	
}
class Rechthoek
{	CssColor kleur;
	int topLeftX, topLeftY, breedte, hoogte;
	int bbFactor = 4;
	Polygon rechthoek;
	//Rectangle outerBB;
	//Rectangle innerBB;
	Polygon outerRechthoek;
	Polygon innerRechthoek;
	double cx, cy;
	AffineTransform at = new AffineTransform();
	
	double rotation = 0;

	//double m00 = 1;
	//double m01 = 0;
	//double m10 = 0;
	//double m11 = 1;
	
	Rectangle handleBox;
	int hbFactor = 4;

	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;

	
	public Rechthoek(CssColor c, int x, int y, int w, int h)
	{
		kleur = c;
		topLeftX = x;
		topLeftY = y;
		breedte = w;
		hoogte = h;
		
		cx = topLeftX + ((double) breedte) / 2;
		cy = topLeftY + ((double) hoogte) / 2;
		
		maakRechthoek();
		makeBB();
		makeHandleBox();
		
		
	}

	public void maakRechthoek()
	{
		rechthoek = new Polygon();
		rechthoek.addPoint(topLeftX, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY + hoogte);
		rechthoek.addPoint(topLeftX, topLeftY + hoogte);
		
		makeBB();
		
		rechthoek.rotate(rotation, cx, cy);
		outerRechthoek.rotate(rotation, cx, cy);
		innerRechthoek.rotate(rotation, cx, cy);
		
		makeHandleBox();
	}
	
	public void makeHandleBox()
	{
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < outerRechthoek.aantalPunten; pCnt++)
		{
			if (outerRechthoek.puntenX[pCnt] < minX)
				minX = outerRechthoek.puntenX[pCnt];
			if (outerRechthoek.puntenX[pCnt] > maxX)
				maxX = outerRechthoek.puntenX[pCnt];
			if (outerRechthoek.puntenY[pCnt] < minY)
				minY = outerRechthoek.puntenY[pCnt];
			if (outerRechthoek.puntenY[pCnt] > maxY)
				maxY = outerRechthoek.puntenY[pCnt];
		}
		
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);	
		
		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		if (KladjeGWTVeld.roteren)
			makeRotateHandles();
		
	}
	
	public void makeScaleHandles()
	{
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}

	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}

	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width, // - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor, 
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
		
	}
	
	
	public void makeBB()
	{
		outerRechthoek = new Polygon();
		outerRechthoek.addPoint(topLeftX - bbFactor, topLeftY - bbFactor);
		outerRechthoek.addPoint(topLeftX + breedte + bbFactor, topLeftY - bbFactor);
		outerRechthoek.addPoint(topLeftX + breedte + bbFactor, topLeftY + hoogte + bbFactor);
		outerRechthoek.addPoint(topLeftX - bbFactor, topLeftY + hoogte + bbFactor);
		
		innerRechthoek = new Polygon();
		innerRechthoek.addPoint(topLeftX + bbFactor, topLeftY + bbFactor);
		innerRechthoek.addPoint(topLeftX + breedte - bbFactor, topLeftY + bbFactor);
		innerRechthoek.addPoint(topLeftX + breedte - bbFactor, topLeftY + hoogte - bbFactor);
		innerRechthoek.addPoint(topLeftX + bbFactor, topLeftY + hoogte - bbFactor);		
		
	}

/*	
	public void makeOuterInner()
	{
		outerRechthoek = new Polygon();
		outerRechthoek.addPoint(outerBB.x, outerBB.y);
		outerRechthoek.addPoint(outerBB.x + outerBB.width, outerBB.y);
		outerRechthoek.addPoint(outerBB.x + outerBB.width, outerBB.y + outerBB.height);
		outerRechthoek.addPoint(outerBB.x, outerBB.y + outerBB.height);
		
		innerRechthoek = new Polygon();
		innerRechthoek.addPoint(innerBB.x, innerBB.y);
		innerRechthoek.addPoint(innerBB.x + innerBB.width, innerBB.y);
		innerRechthoek.addPoint(innerBB.x + innerBB.width, innerBB.y + innerBB.height);
		innerRechthoek.addPoint(innerBB.x, innerBB.y + innerBB.height);
		
		
	}
*/	
	
	public void rotate(double rotateStep)
	{	
		rotation += rotateStep;
		
		//double m00New  = Math.cos(rotateStep) * m00 - Math.sin(rotateStep) * m10;
		//double m10New  = Math.sin(rotateStep) * m00 + Math.cos(rotateStep) * m10;
		//double m01New = Math.cos(rotateStep) * m01 - Math.sin(rotateStep) * m11;
		//double m11New = Math.sin(rotateStep) * m01 + Math.cos(rotateStep) * m11;
		
		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);
		
		rechthoek.rotate(rotateStep, cx, cy);
		outerRechthoek.rotate(rotateStep, cx, cy);
		innerRechthoek.rotate(rotateStep, cx, cy);
		
		makeHandleBox();

	}
	
	public void rotate(double rotateStep, double dx, double dy)
	{	

		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
				  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);

		rechthoek.rotate(rotateStep, dx, dy);
		outerRechthoek.rotate(rotateStep, dx, dy);
		innerRechthoek.rotate(rotateStep, dx, dy);
		
		makeHandleBox();

	}

	public void scale(double scaleStep)
	{	
//		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
//		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
//		breedte = (int) Math.round(scaleStep * breedte);
//		hoogte = (int) Math.round(scaleStep * hoogte);
		
		//maakRechthoek();
		
		//m00 *= scaleStep;
		//m10 *= scaleStep;
		//m01 *= scaleStep;
		//m11 *= scaleStep;

		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);
		
		rechthoek.scale(scaleStep, cx, cy);
		outerRechthoek.scale(scaleStep, cx, cy);
		innerRechthoek.scale(scaleStep, cx, cy);
		
		makeHandleBox();
		
	}
	public void scale(double scaleStep, double dx, double dy)
	{	
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);

		rechthoek.scale(scaleStep, dx, dy);
		outerRechthoek.scale(scaleStep, dx, dy);
		innerRechthoek.scale(scaleStep, dx, dy);
		
		makeHandleBox();
		
	}
	
	public void scale(double sx, double sy)
	{
//		topLeftX = (int) Math.round(sx * topLeftX + (1 - sx) * cx);
//		topLeftY = (int) Math.round(sy * topLeftY + (1 - sy) * cy);
//		breedte = (int) Math.round(sx * breedte);
//		hoogte = (int) Math.round(sy * hoogte);

		//maakRechthoek();
		
		//m00 *= sx;
		//m01 *= sx;
		//m10 *= sy;
		//m11 *= sy;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);
		
		rechthoek.scale(sx, sy, cx, cy);
		outerRechthoek.scale(sx, sy, cx, cy);
		innerRechthoek.scale(sx,sy, cx, cy);
		
		makeHandleBox();
		
	}
	
	public void scale(double sx, double sy, double dx, double dy)
	{
		cx = sx * cx + (1 - sx) * dx;
		cy = sy * cy + (1 - sy) * dy;

		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);

		rechthoek.scale(sx, sy, dx, dy);
		outerRechthoek.scale(sx, sy, dx, dy);
		innerRechthoek.scale(sx,sy, dx, dy);
		
		makeHandleBox();
		
	}

	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("topLeftX", new Integer(topLeftX));
		h.put("topLeftY", new Integer(topLeftY));
		h.put("breedte", new Integer(breedte));
		h.put("hoogte", new Integer(hoogte));

		//h.put("rotation", new Double(rotation));
	
		h.put("m00", new Double(at.m00));
		h.put("m10", new Double(at.m10));
		h.put("m01", new Double(at.m01));
		h.put("m11", new Double(at.m11));
		h.put("b0", new Double(at.b0));
		h.put("b1", new Double(at.b1));

		
		return h;
	}
	
	public static Rechthoek setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		int topLeftX = 0; 
		int topLeftY = 0;
		int breedte = 0;
		int hoogte = 0;
		double rotation = 0;

		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("topLeftX"))
			topLeftX = h.getInt("topLeftX");
		if (h.containsKey("topLeftY"))
			topLeftY = h.getInt("topLeftY");

		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		
		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");
		
		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");


		
		Rechthoek rechthoek = new Rechthoek(kleur, topLeftX, topLeftY, breedte, hoogte);
		rechthoek.rotation = rotation;
		rechthoek.maakRechthoek();

		//if (h.containsKey("rotation"))
		//{	rechthoek.rotate(rotation);
		//}
		//else 
		if (h.containsKey("b0"))
		{	rechthoek.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else
		{	rechthoek.transformBy(m00, m01, m10, m11);
		}
		
		return rechthoek;
	}

	public void transformBy(double m00, double m01, double m10, double m11)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);
		
		rechthoek.transformBy(m00, m01, m10, m11, cx, cy);
		outerRechthoek.transformBy(m00, m01, m10, m11, cx, cy);
		innerRechthoek.transformBy(m00, m01, m10, m11, cx, cy);

		makeHandleBox();
		
	}
	
	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		rechthoek.transformBy(at);
		outerRechthoek.transformBy(at);
		innerRechthoek.transformBy(at);
		
		makeHandleBox();
		
	}
	
	public void teken(Context2d g)
	{
		//makeRechthoek();
		//rechthoek.rotate(rotation, cx, cy);

		g.setStrokeStyle(kleur);		

		g.beginPath();		
		g.moveTo(rechthoek.doubleX[0], rechthoek.doubleY[0]);
		for (int k = 1; k < rechthoek.aantalPunten; k++) 
		{	g.lineTo(rechthoek.doubleX[k], rechthoek.doubleY[k]);
		}
		g.lineTo(rechthoek.doubleX[0], rechthoek.doubleY[0]);
		g.closePath();
		g.stroke();
		
	}

	public void tekenHandleBox(Context2d g)
	{

		//float[] dash = new float[2];
		//dash[0] = 2;
		//dash[1] = 2;
		//g.setStroke(new BasicStroke(1.0f, 2, 0, 10.0f, dash, 0.0f));
		
		//g.setColor(KladjeVeld.hbColor);

		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		//g.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topRightHandle);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topLeftHandle);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomRightHandle);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomLeftHandle);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x + handleBox.width, // - 2 * hbFactor,
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
		
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x - 4 * hbFactor, 
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
			
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}

	}
	
	public void tekenBB(Context2d g)
	{

		//makeOuterInner();
		//outerRechthoek.rotate(rotation, cx, cy);
		//innerRechthoek.rotate(rotation, cx, cy);
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);
		
		g.beginPath();
		g.moveTo(outerRechthoek.doubleX[0], outerRechthoek.doubleY[0]);
		for (int k = 1; k < outerRechthoek.aantalPunten; k++) 
		{	g.lineTo(outerRechthoek.doubleX[k], outerRechthoek.doubleY[k]);
		}
		g.lineTo(outerRechthoek.doubleX[0], outerRechthoek.doubleY[0]);
		g.closePath();
		g.stroke();

		g.beginPath();		
		g.moveTo(innerRechthoek.doubleX[0], innerRechthoek.doubleY[0]);
		for (int k = 1; k < innerRechthoek.aantalPunten; k++) 
		{	g.lineTo(innerRechthoek.doubleX[k], innerRechthoek.doubleY[k]);
		}
		g.lineTo(innerRechthoek.doubleX[0], innerRechthoek.doubleY[0]);
		g.closePath();
		g.stroke();
		
		g.setLineWidth(1.5d);
		
	}
	
/*	
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
*/
/*	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
*/	
	public boolean bbContains(int x, int y)
	{
		
		return outerRechthoek.contains(x, y) && !innerRechthoek.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		//topLeftX += dx;
		//topLeftY += dy;
		cx += dx;
		cy += dy;
		
		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		rechthoek.translate(dx, dy);
		outerRechthoek.translate(dx, dy);
		innerRechthoek.translate(dx, dy);
		
		makeHandleBox();
		
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;

		boolean isContainedIn = true;
		for (int cnt = 0; cnt < outerRechthoek.aantalPunten; cnt++)
		{
			isContainedIn = isContainedIn && 
							r.contains(outerRechthoek.puntenX[cnt], outerRechthoek.puntenY[cnt]);
		}
		
		return isContainedIn;
	}
	
}
class Ellips
{	CssColor kleur;
	int topLeftX, topLeftY, breedte, hoogte;
	int bbFactor = 4;
	Polygon ellips;
	//Rectangle outerBB, innerBB;
	Polygon outerEllips, innerEllips;
	double cx, cy;
	AffineTransform at = new AffineTransform();
	
	double rotation = 0;
	
	//double m00 = 1;
	//double m01 = 0;
	//double m10 = 0;
	//double m11 = 1;
	
	int steps = 75;
	
	Rectangle handleBox;
	int hbFactor = 4;

	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;
	
	
	public Ellips(CssColor c, int x, int y, int w, int h)
	{
		kleur = c;
		topLeftX = x;
		topLeftY = y;
		breedte = w;
		hoogte = h;
		
		cx = topLeftX + ((double) breedte) / 2;
		cy = topLeftY + ((double) hoogte) / 2;
		
		makeEllips();
		
	}

	public void makeEllips()
	{
		double angleStep = 2 * Math.PI / steps;
		
		ellips = new Polygon();
		for (int pCnt = 0; pCnt <= steps; pCnt++)
		{
			double x = cx + (breedte / 2) * Math.cos(pCnt * angleStep);
			double y = cy - (hoogte / 2) * Math.sin(pCnt * angleStep);
			ellips.addPoint(x, y);
		}
		
		makeOuterInner();
		
		ellips.rotate(rotation, cx, cy);
		outerEllips.rotate(rotation, cx, cy);
		innerEllips.rotate(rotation, cx, cy);
		
		makeHandleBox();
	}
	

	public void makeHandleBox()
	{
		double minXD = 1000;
		double maxXD = -100;
		double minYD = 1000;
		double maxYD = -100;
		for (int pCnt = 0; pCnt < outerEllips.aantalPunten; pCnt++)
		{
			if (outerEllips.doubleX[pCnt] < minXD)
				minXD = outerEllips.doubleX[pCnt];
			if (outerEllips.doubleX[pCnt] > maxXD)
				maxXD = outerEllips.doubleX[pCnt];
			if (outerEllips.doubleY[pCnt] < minYD)
				minYD = outerEllips.doubleY[pCnt];
			if (outerEllips.doubleY[pCnt] > maxYD)
				maxYD = outerEllips.doubleY[pCnt];
		}
		
		int minX = (int) Math.round(minXD);
		int maxX = (int) Math.round(maxXD);
		int minY = (int) Math.round(minYD);
		int maxY = (int) Math.round(maxYD);
				
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);	
		
		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		if (KladjeGWTVeld.roteren)
			makeRotateHandles();
		
	}
	
	public void makeScaleHandles()
	{
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}

	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}

	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width, // - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor, 
				                         handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
		
		
	}
	
	
	public void makeOuterInner()
	{
		double angleStep = 2 * Math.PI / steps;
		
		outerEllips = new Polygon();
		for (int pCnt = 0; pCnt <= steps; pCnt++)
		{
			double x = cx + (breedte/2 + bbFactor) * Math.cos(pCnt * angleStep);
			double y = cy - (hoogte/2 + bbFactor) * Math.sin(pCnt * angleStep);
			outerEllips.addPoint(x, y);
		}

		innerEllips = new Polygon();
		for (int pCnt = 0; pCnt <= steps; pCnt++)
		{
			double x = cx + (breedte/2 - bbFactor) * Math.cos(pCnt * angleStep);
			double y = cy - (hoogte/2 - bbFactor) * Math.sin(pCnt * angleStep);
			innerEllips.addPoint(x, y);
		}
		
	}

/*	
	public void makeBB()
	{
		outerBB = new Rectangle(topLeftX - bbFactor, topLeftY - bbFactor, 
			    breedte + 2 * bbFactor, hoogte + 2 * bbFactor);
		innerBB = new Rectangle(topLeftX + bbFactor, topLeftY + bbFactor, 
			    breedte - 2 * bbFactor, hoogte - 2 * bbFactor);
		
	}
*/
	
	public void rotate(double rotateStep)
	{	rotation += rotateStep;

		//double m00New  = Math.cos(rotateStep) * m00 - Math.sin(rotateStep) * m10;
		//double m10New  = Math.sin(rotateStep) * m00 + Math.cos(rotateStep) * m10;
		//double m01New = Math.cos(rotateStep) * m01 - Math.sin(rotateStep) * m11;
		//double m11New = Math.sin(rotateStep) * m01 + Math.cos(rotateStep) * m11;

		//m00 = m00New;
		//m01 = m01New;
		//m10 = m10New;
		//m11 = m11New;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);

	
		ellips.rotate(rotateStep, cx, cy);
		outerEllips.rotate(rotateStep, cx, cy);
		innerEllips.rotate(rotateStep, cx, cy);

		makeHandleBox();

	}
	public void rotate(double rotateStep, double dx, double dy)
	{	//rotation += rotateStep;

		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
				  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);

		ellips.rotate(rotateStep, dx, dy);
		outerEllips.rotate(rotateStep, dx, dy);
		innerEllips.rotate(rotateStep, dx, dy);

		makeHandleBox();

	}
	
	public void scale(double scaleStep)
	{	
//		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
//		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
//		breedte = (int) Math.round(scaleStep * breedte);
//		hoogte = (int) Math.round(scaleStep * hoogte);
		
//		makeEllips();
		
		//m00 *= scaleStep;
		//m01 *= scaleStep;
		//m10 *= scaleStep;
		//m11 *= scaleStep;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);


		ellips.scale(scaleStep, cx, cy);
		outerEllips.scale(scaleStep, cx, cy);
		innerEllips.scale(scaleStep, cx, cy);
		

		makeHandleBox();

		
	}
	
	public void scale(double scaleStep, double dx, double dy)
	{	
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);

		ellips.scale(scaleStep, dx, dy);
		outerEllips.scale(scaleStep, dx, dy);
		innerEllips.scale(scaleStep, dx, dy);
		

		makeHandleBox();
		
	}

	public void scale(double sx, double sy)
	{	
//		topLeftX = (int) Math.round(sx * topLeftX + (1 - sx) * cx);
//		topLeftY = (int) Math.round(sy * topLeftY + (1 - sy) * cy);
//		breedte = (int) Math.round(sx * breedte);
//		hoogte = (int) Math.round(sy * hoogte);
		
//		makeEllips();
		
		//m00 *= sx;
		//m01 *= sx;
		//m10 *= sy;
		//m11 *= sy;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);


		ellips.scale(sx, sy, cx, cy);
		outerEllips.scale(sx, sy, cx, cy);
		innerEllips.scale(sx, sy, cx, cy);
		

		makeHandleBox();

		
	}
	
	public void scale(double sx, double sy, double dx, double dy)
	{	
		cx = sx * cx + (1 - sx) * dx;
		cy = sy * cy + (1 - sy) * dy;

		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);

		ellips.scale(sx, sy, dx, dy);
		outerEllips.scale(sx, sy, dx, dy);
		innerEllips.scale(sx, sy, dx, dy);
		
		makeHandleBox();
		
	}

	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("topLeftX", new Integer(topLeftX));
		h.put("topLeftY", new Integer(topLeftY));
		h.put("breedte", new Integer(breedte));
		h.put("hoogte", new Integer(hoogte));

//		h.put("rotation", new Double(rotation));
		
		h.put("m00", new Double(at.m00));
		h.put("m10", new Double(at.m10));
		h.put("m01", new Double(at.m01));
		h.put("m11", new Double(at.m11));
		h.put("b0", new Double(at.b0));
		h.put("b1", new Double(at.b1));

	
		return h;
	}
	
	public static Ellips setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		int topLeftX = 0; 
		int topLeftY = 0;
		int breedte = 0;
		int hoogte = 0;
		double rotation = 0;

		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("topLeftX"))
			topLeftX = h.getInt("topLeftX");
		if (h.containsKey("topLeftY"))
			topLeftY = h.getInt("topLeftY");

		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");

		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");
		
		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");


		
		Ellips ellips = new Ellips(kleur, topLeftX, topLeftY, breedte, hoogte);
		ellips.rotation = rotation;
		ellips.makeEllips();

		//if (h.containsKey("rotation"))
		//{	ellips.rotate(rotation);
		//}
		//else 
		if (h.containsKey("b0"))
		{	ellips.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else
		{	ellips.transformBy(m00, m01, m10, m11);
		}
		
		return ellips;
	}

	public void transformBy(double m00, double m01, double m10, double m11)
	{
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);
		
		ellips.transformBy(m00, m01, m10, m11, cx, cy);
		outerEllips.transformBy(m00, m01, m10, m11, cx, cy);
		innerEllips.transformBy(m00, m01, m10, m11, cx, cy);
		
		makeHandleBox();
		
	}

	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		ellips.transformBy(at);
		outerEllips.transformBy(at);
		innerEllips.transformBy(at);
		
		makeHandleBox();
		
	}
	
	public void teken(Context2d g)
	{
		//makeEllips();
		//ellips.rotate(rotation, cx, cy);
		
		g.setStrokeStyle(kleur);
		
		g.beginPath();		
		g.moveTo(ellips.doubleX[0], ellips.doubleY[0]);
		for (int k = 1; k < ellips.aantalPunten; k++) 
		{	g.lineTo(ellips.doubleX[k], ellips.doubleY[k]);
		}
		g.lineTo(ellips.doubleX[0], ellips.doubleY[0]);
		g.closePath();
		g.stroke();


	}

	public void tekenHandleBox(Context2d g)
	{

		//float[] dash = new float[2];
		//dash[0] = 2;
		//dash[1] = 2;
		//g.setStroke(new BasicStroke(1.0f, 2, 0, 10.0f, dash, 0.0f));
		
		//g.setColor(KladjeVeld.hbColor);

		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		//g.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topRightHandle);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topLeftHandle);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomRightHandle);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomLeftHandle);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x + handleBox.width, // - 2 * hbFactor,
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
		
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x - 4 * hbFactor, 
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
			
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}

	}

	
	public void tekenBB(Context2d g)
	{
		//makeOuterInner();
		//outerEllips.rotate(rotation, cx, cy);
		//innerEllips.rotate(rotation, cx, cy);
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);
		
		g.beginPath();
		g.moveTo(outerEllips.doubleX[0], outerEllips.doubleY[0]);
		for (int k = 1; k < outerEllips.aantalPunten; k++) 
		{	g.lineTo(outerEllips.doubleX[k], outerEllips.doubleY[k]);
		}
		g.lineTo(outerEllips.doubleX[0], outerEllips.doubleY[0]);
		g.closePath();
		g.stroke();

		g.beginPath();		
		g.moveTo(innerEllips.doubleX[0], innerEllips.doubleY[0]);
		for (int k = 1; k < innerEllips.aantalPunten; k++) 
		{	g.lineTo(innerEllips.doubleX[k], innerEllips.doubleY[k]);
		}
		g.lineTo(innerEllips.doubleX[0], innerEllips.doubleY[0]);
		g.closePath();
		g.stroke();
		
		g.setLineWidth(1.5d);

	}

/*	
	boolean ellipsContains(int x, int y, Rectangle r)
	{
		
		int rx = inverseTransformX(x, y);
		int ry = inverseTransformY(x, y);
		
		double a = ((double) r.width) / 2;
		double b = ((double) r.height) / 2;
		double cx = r.x + a;
		double cy = r.y + b;
		double px = ((double) rx) - cx;
		double py = ((double) ry) - cy;
		
		//px^2/a^2+py^2/b^2<1
		
		double inside = px*px/(a*a) + py*py/(b*b);
		
		boolean contains = inside < 1;
		return contains;
	}
*/	
	
/*	
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
*/	
/*	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
*/	
	public boolean bbContains(int x, int y)
	{
		
		return outerEllips.contains(x, y) && !innerEllips.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		//topLeftX += dx;
		//topLeftY += dy;
		
		ellips.translate(dx, dy);
		outerEllips.translate(dx, dy);
		innerEllips.translate(dx, dy);
		
		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		cx += dx;
		cy += dy;
		
		makeHandleBox();

		
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;
		
		boolean isContainedIn = true;
		for (int cnt = 0; cnt < outerEllips.aantalPunten; cnt++)
		{
			isContainedIn = isContainedIn && 
							r.contains(outerEllips.puntenX[cnt], outerEllips.puntenY[cnt]);
		}
		
		return isContainedIn;
	}
	
}

class TekstElement
{
	CssColor kleur;
	String tekst;
	int xPos, yPos;
	int breedte, hoogte, ascent;
	int bbFactor = 0;
	Rectangle bb;
	Polygon bb2;
	double cx, cy;
	
	double rotation; 
	double scaleX = 1;
	double scaleY = 1;
	
	AffineTransform at = new AffineTransform();
	
	int tekstX, tekstY;
	Rectangle handleBox;
	int hbFactor = 4;
	
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;

	
	public TekstElement(CssColor c, String t, int x, int y)
	{
		kleur = c;
		tekst = new String(t);
		xPos = x;
		yPos = y;
		tekstX = x;
		tekstY = y;
				
//System.out.println("te = " + xPos + " " + yPos + " " + tekst);		
		//cx = xPos + ((double) breedte) / 2;
		//cy = yPos + ((double) hoogte) / 2;
		
		//makeBB();
	
	}

	public void zetTekst(String t, Context2d g)
	{
		tekst = new String(t);
		
		//breedte = KladjeVeld.tekstFM.stringWidth(tekst);
		//cx = xPos + ((double) breedte) / 2;
		//cy = yPos + ((double) hoogte) / 2;
		
		//makeBB();
		
		TextMetrics tm = g.measureText(tekst);
		breedte = (int) Math.round(tm.getWidth());
		hoogte = 20;
		
		makeBB();
		
	}

	
	public void makeBB()
	{
		
		cx = xPos + ((double) breedte) / 2;
		cy = yPos + ((double) hoogte) / 2;
		
		bb = new Rectangle(xPos - bbFactor, yPos - bbFactor, 
						   breedte + 2 * bbFactor, hoogte + 2 * bbFactor);
		
		
		bb2 = new Polygon();
		bb2.addPoint(xPos - bbFactor, yPos - bbFactor);
		bb2.addPoint(xPos - bbFactor + breedte + 2 * bbFactor, yPos - bbFactor);
		bb2.addPoint(xPos - bbFactor + breedte + 2 * bbFactor, yPos - bbFactor + hoogte + 2 * bbFactor);
		bb2.addPoint(xPos - bbFactor, yPos - bbFactor + hoogte + 2 * bbFactor);

		makeHandleBox();
	}

	public void makeHandleBox()
	{
		//int minX = bb.x;
		//int maxX = bb.x + bb.width;
		//int minY = bb.y;
		//int maxY = bb.y+bb.height;

		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < bb2.aantalPunten; pCnt++)
		{
			if (bb2.puntenX[pCnt] < minX)
				minX = bb2.puntenX[pCnt];
			if (bb2.puntenX[pCnt] > maxX)
				maxX = bb2.puntenX[pCnt];
			if (bb2.puntenY[pCnt] < minY)
				minY = bb2.puntenY[pCnt];
			if (bb2.puntenY[pCnt] > maxY)
				maxY = bb2.puntenY[pCnt];
		}

		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);

		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		//if (KladjeGWTVeld.roteren)
		//	makeRotateHandles();
		
	}
	
	public void makeScaleHandles()
	{
/*		
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
*/
/*		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
*/		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		

/*
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
*/		
	}

	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}
	
	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width, // - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);

		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor,
				                         handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
										 
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
		
	}
	
	public void rotate(double rotateStep)
	{	
		rotation += rotateStep;
		
/*		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
			  Math.sin(rotateStep), Math.cos(rotateStep), 
			  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
			  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);
*/		

	}

	public void rotate(double rotateStep, double dx, double dy)
	{	
		double cxCopy = cx;
		double cyCopy = cy;
		double rotatedCxCopy = Math.cos(rotateStep) * cxCopy - Math.sin(rotateStep) * cyCopy + 
			                   dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy;
		double rotatedCyCopy = Math.sin(rotateStep) * cxCopy + Math.cos(rotateStep) * cyCopy + 
							   dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy;
		int deltax = (int) Math.round(rotatedCxCopy - cxCopy);
		int deltay = (int) Math.round(rotatedCyCopy - cyCopy);
		
		translate(deltax, deltay);
		
		
		
/*		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
			  Math.sin(rotateStep), Math.cos(rotateStep), 
			  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
			  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);
*/		
	
	}

	public void scale(double scaleStep)
	{	

		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);

	}
	
	public void scale(double scaleStep, double dx, double dy)
	{	AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);
	
	}
	
	public void scale(double sx, double sy)
	{	AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);

	}
	
	public void scale(double sx, double sy, double dx, double dy)
	{	AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);
	
	}
	
	public HashMap<String, Object> getState()
	{	
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("tekst", new String(tekst));
		h.put("xPos", new Integer(xPos));
		h.put("yPos", new Integer(yPos));
		h.put("m00GWT", new Double(at.m00));
		h.put("m10GWT", new Double(at.m10));
		h.put("m01GWT", new Double(at.m01));
		h.put("m11GWT", new Double(at.m11));
		h.put("b0GWT", new Double(at.b0));
		h.put("b1GWT", new Double(at.b1));
		
		h.put("gwtState", new String("gwtState"));
		
System.out.println("get " + tekst + " " + at.toString());		

	
		return h;
	}

	public static TekstElement setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		String tekst = new String("");
		int xPos = 0;
		int yPos = 0;
		double rotation = 0;
		double scaleX = 1;
		double scaleY = 1;
		
		int breedteGWT = 20;
		int hoogteGWT = 20;
		
		//double m00 = 1;
		//double m01 = 0;
		//double m10 = 0;
		//double m11 = 1;
		//double b0 = 0;
		//double b1 = 0;

		double m00GWT = 1;
		double m01GWT = 0;
		double m10GWT = 0;
		double m11GWT = 1;
		double b0GWT = 0;
		double b1GWT = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("tekst"))
			tekst = h.getString("tekst");
		if (h.containsKey("xPos"))
			xPos = h.getInt("xPos");
		if (h.containsKey("yPos"))
			yPos = h.getInt("yPos");
		
		if (h.containsKey("scaleX"))
			scaleX = h.getDouble("scaleX");
		if (h.containsKey("scaleY"))
			scaleY = h.getDouble("scaleY");

/*		
		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");
*/
		if (h.containsKey("m00GWT"))
			m00GWT = h.getDouble("m00GWT");
		if (h.containsKey("m10GWT"))
			m10GWT = h.getDouble("m10GWT");
		if (h.containsKey("m01GWT"))
			m01GWT = h.getDouble("m01GWT");
		if (h.containsKey("m11GWT"))
			m11GWT = h.getDouble("m11GWT");
		if (h.containsKey("b0GWT"))
			b0GWT = h.getDouble("b0GWT");
		if (h.containsKey("b1GWT"))
			b1GWT = h.getDouble("b1GWT");
		
//System.out.println("b0GWT = " + b0GWT);		
//System.out.println("b1GWT = " + b1GWT);		
		
		if (h.containsKey("breedteGWT"))
			breedteGWT = h.getInt("breedteGWT");
		if (h.containsKey("hoogteGWT"))
			hoogteGWT = h.getInt("hoogteGWT");
		
		TekstElement tekstElement = new TekstElement(kleur, tekst, xPos, yPos);
		
		if (h.containsKey("m00GWT"))
		{	
			tekstElement.transformByGWT(m00GWT, m01GWT, m10GWT, m11GWT, b0GWT, b1GWT);
		}
		else
		{	
			//tekstElement.scale(scaleX, scaleY);
		
		}
		
		return tekstElement;
	}

	public void transformByGWT(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);

if (tekst.equals("PPP"))		
System.out.println("set " + tekst + " " + at.toString());
//System.out.println("set xPos " + xPos);
//System.out.println("set yPos " + yPos);
	}
	
	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1, int bGWT, int hGWT)
	{
		//at = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		String fontString = "bold 14px arial, sans-serif";
		KladjeGWTVeld.gIm.setFont(fontString);
		
		TextMetrics tm = KladjeGWTVeld.gIm.measureText(tekst);
		int atbreedte = (int) Math.round(tm.getWidth());
		int athoogte = 15;
		
System.out.println("bGWT = " + bGWT);
System.out.println("hGWT = " + hGWT);
		
		Polygon atbb = new Polygon();
		atbb.addPoint(xPos, yPos);
		atbb.addPoint(xPos + atbreedte, yPos);
		atbb.addPoint(xPos + atbreedte, yPos + athoogte);
		atbb.addPoint(xPos, yPos + athoogte);
		
		AffineTransform tat = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		atbb.transformBy(tat);

System.out.println(tekst);		
System.out.println("atbb0 = " + atbb.geefPuntXD(0));
System.out.println("atbb1 = " + atbb.geefPuntXD(1));
System.out.println("atbb2 = " + atbb.geefPuntXD(2));
System.out.println("atbb3 = " + atbb.geefPuntXD(3));
		
		at = new AffineTransform(1,0,0,1,0,0);
		
		cx = (atbb.geefPuntXD(0)+atbb.geefPuntXD(2)) / 2;
		cy = (atbb.geefPuntYD(0)+atbb.geefPuntYD(2)) / 2;
		
		// ad hoc correctie
		double dxcorr = -7;
		double dycorr = -6;
		double dx = cx - (xPos + atbreedte / 2) + dxcorr;
		double dy = cy - (yPos + athoogte / 2) + dycorr;
		
		AffineTransform trans = new AffineTransform (1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		
		double xLength = Math.sqrt((atbb.geefPuntXD(1)-atbb.geefPuntXD(0))*(atbb.geefPuntXD(1)-atbb.geefPuntXD(0))+
						           (atbb.geefPuntYD(1)-atbb.geefPuntYD(0))*(atbb.geefPuntYD(1)-atbb.geefPuntYD(0)));
		double yLength = Math.sqrt((atbb.geefPuntXD(2)-atbb.geefPuntXD(1))*(atbb.geefPuntXD(2)-atbb.geefPuntXD(1))+
		           				   (atbb.geefPuntYD(2)-atbb.geefPuntYD(1))*(atbb.geefPuntYD(2)-atbb.geefPuntYD(1)));
		
		double sx = xLength / atbreedte;
		double sy = yLength / athoogte;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);
		

		
	}

	public void setCenter()
	{
		cx = (bb2.geefPuntX(0) + bb2.geefPuntX(2)) / 2;
		cy = (bb2.geefPuntY(0) + bb2.geefPuntY(2)) / 2;
	}

	public void teken(Context2d g)
	{
		String fontString = "bold 14px arial, sans-serif";
		g.setFont(fontString);
		
		TextMetrics tm = g.measureText(tekst);
		breedte = (int) Math.round(tm.getWidth());
		hoogte = 15;
		
		makeBB();
		bb2.transformBy(at);
		setCenter();
		makeHandleBox();

		g.setTransform(at.m00, at.m01, at.m10, at.m11, at.b0, at.b1);
		//g.transform(at.m00, at.m01, at.m10, at.m11, at.b0, at.b1);
		//g.rotate(rotation);
		
//System.out.println("ta = " + g.getTextAlign());
//System.out.println("tbl = " + g.getTextBaseline());

		g.setTextBaseline(Context2d.TextBaseline.BOTTOM);
		g.setTextAlign(Context2d.TextAlign.START);
		
		//g.setStrokeStyle(kleur);
		//g.setFillStyle(kleur);
		g.setFillStyle(kleur);
		
		g.fillText(tekst, xPos, yPos + 15, breedte);
		
		g.setTransform(1, 0, 0, 1, 0, 0);
		
		tekenBB(g);
	}

	public void tekenHandleBox(Context2d g)
	{

		//float[] dash = new float[2];
		//dash[0] = 2;
		//dash[1] = 2;
		//g.setStroke(new BasicStroke(1.0f, 2, 0, 10.0f, dash, 0.0f));
		
		//g.setColor(KladjeVeld.hbColor);

		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		//g.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topRightHandle);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(topLeftHandle);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomRightHandle);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawPolygon(bottomLeftHandle);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x + handleBox.width, // - 2 * hbFactor,
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
		
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	//g.setColor(KladjeVeld.hbColor);
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			//g.drawOval(handleBox.x - 4 * hbFactor, 
			//		   handleBox.y + handleBox.height/2 - 2 * hbFactor, 
			//		   4 * hbFactor, 4 * hbFactor);
			
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}


	}
	
	public void tekenBB(Context2d g)
	{
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.moveTo(bb2.doubleX[0], bb2.doubleY[0]);
		for (int k = 1; k < bb2.aantalPunten; k++) 
		{	g.lineTo(bb2.doubleX[k], bb2.doubleY[k]);
		}
		g.lineTo(bb2.doubleX[0], bb2.doubleY[0]);
		g.closePath();
		g.stroke();
		
		g.setLineWidth(1.5d);

	}

	public boolean bbContains(int x, int y)
	{
		//return bb.contains(x, y);
		return bb2.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		AffineTransform trans = new AffineTransform (1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		//xPos += dx;
		//yPos += dy;
		bb.translate(dx, dy);
		cx += dx;
		cy += dy;
		
		bb2.translate(dx, dy);
		
		makeHandleBox();
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;

		//boolean isContainedIn = r.contains(bb.x, bb.y) && r.contains(bb.x + bb.width, bb.y + bb.height);
		
		boolean isContainedIn = true;
		
		int topLeftX = bb2.geefPuntX(0);
		int topRightX = bb2.geefPuntX(1);
		int bottomRightX = bb2.geefPuntX(2);
		int bottomLeftX = bb2.geefPuntX(3);

		int topLeftY = bb2.geefPuntY(0);
		int topRightY = bb2.geefPuntY(1);
		int bottomRightY = bb2.geefPuntY(2);
		int bottomLeftY = bb2.geefPuntY(3);
		
		int topMiddleX = (topLeftX + topRightX) / 2;
		int topMiddleY = (topLeftY + topRightY) / 2;
		int rightMiddleX = (topRightX + bottomRightX) / 2;
		int rightMiddleY = (topRightY + bottomRightY) / 2;
		int bottomMiddleX = (bottomLeftX + bottomRightX) / 2;
		int bottomMiddleY = (bottomLeftY + bottomRightY) / 2;
		int leftMiddleX = (topLeftX + bottomLeftX) / 2;
		int leftMiddleY = (topLeftY + bottomLeftY) / 2;
		
		isContainedIn = isContainedIn && r.contains(topMiddleX, topMiddleY);
		isContainedIn = isContainedIn && r.contains(rightMiddleX, rightMiddleY);
		isContainedIn = isContainedIn && r.contains(bottomMiddleX, bottomMiddleY);
		isContainedIn = isContainedIn && r.contains(leftMiddleX, leftMiddleY);

		
		return isContainedIn;
	}
	
}