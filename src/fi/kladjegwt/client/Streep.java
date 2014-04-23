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
	double rotation = 0;

	Rectangle handleBox;
	int hbFactor = 4;
	int breedte, hoogte;
	
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;
	
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
		}
		
		maakStreep();

	}

	public Streep(CssColor c, int[] ptX, int[] ptY)
	{	kleur = c;
		puntenXD = new double[ptX.length];
		puntenYD = new double[ptY.length];

		for (int cnt = 0; cnt < ptX.length; cnt++) 
		{	puntenXD[cnt] = ptX[cnt];
			puntenYD[cnt] = ptY[cnt];
		}
		
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
		bb.rotate(rotateStep, cx, cy);
	
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
		
	}
	
	public void scale(double scaleStep)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			puntenXD[pCnt] = scaleStep * puntenXD[pCnt] + (1 - scaleStep) * cx;
			puntenYD[pCnt] = scaleStep * puntenYD[pCnt] + (1 - scaleStep) * cy;
		}
		
		
		maakStreep();
		
		makeHandleBox();
		
	}

	public void scale(double scaleStepX, double scaleStepY)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			puntenXD[pCnt] = scaleStepX * puntenXD[pCnt] + (1 - scaleStepX) * cx;
			puntenYD[pCnt] = scaleStepY * puntenYD[pCnt] + (1 - scaleStepY) * cy;
		}
		
		
		maakStreep();
		
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
		
		h.put("rotation", new Double(rotation));
	
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
		
		return streep;
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

	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
	
	public boolean bbContains(int x, int y)
	{

		return bb.contains(x, y);
	}
	
	public void translate(int dx, int dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	puntenXD[pCnt] += dx;
			puntenYD[pCnt] += dy;
			pXD[pCnt] += dx;
			pYD[pCnt] += dy; 
		}

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
	int fX, fY, tX, tY;
	int bbFactor = 4;
	//Polygon lijn; 
	Polygon bb;
	//Polygon lijnBB;
	double cx, cy;
	double rotation = 0;
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
		fX = (int) Math.round(fXNew + cx);
		fY = (int) Math.round(fYNew + cy);
		tX = (int) Math.round(tXNew + cx);
		tY = (int) Math.round(tYNew + cy);
	
		bb.rotate(rotateStep, cx, cy);
		
		//makeBB();
		//bb2 = rotatePolygon(bb2, rotation, cx, cy);

		makeHandleBox();
	
	}

	public void rotateLijn(double rotation)
	{		
		double fXNew = Math.cos(rotation) * (fX - cx) - Math.sin(rotation) * (fY - cy);
		double fYNew = Math.sin(rotation) * (fX - cx) + Math.cos(rotation) * (fY - cy);
		double tXNew = Math.cos(rotation) * (tX - cx) - Math.sin(rotation) * (tY - cy);
		double tYNew = Math.sin(rotation) * (tX - cx) + Math.cos(rotation) * (tY - cy);
		fX = (int) Math.round(fXNew + cx);
		fY = (int) Math.round(fYNew + cy);
		tX = (int) Math.round(tXNew + cx);
		tY = (int) Math.round(tYNew + cy);
		
	}	

	public void scale(double scaleStep)
	{	
		fromX = (int) Math.round(scaleStep * fromX + (1 - scaleStep) * cx);
		fromY = (int) Math.round(scaleStep * fromY + (1 - scaleStep) * cy);
		toX = (int) Math.round(scaleStep * toX + (1 - scaleStep) * cx);
		toY = (int) Math.round(scaleStep * toY + (1 - scaleStep) * cy);
		
		//makeBB();
		//bb2 = rotatePolygon(bb2, rotation, cx, cy);
		maakLijn();
		
		makeHandleBox();
		
	}

	public void scale(double sx, double sy)
	{	
		fromX = (int) Math.round(sx * fromX + (1 - sx) * cx);
		fromY = (int) Math.round(sy * fromY + (1 - sy) * cy);
		toX = (int) Math.round(sx * toX + (1 - sx) * cx);
		toY = (int) Math.round(sy * toY + (1 - sy) * cy);
		
		//makeBB();
		//bb2 = rotatePolygon(bb2, rotation, cx, cy);
		
		maakLijn();
		makeHandleBox();
		
	}
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("fromX", new Integer(fromX));
		h.put("fromY", new Integer(fromY));
		h.put("toX", new Integer(toX));
		h.put("toY", new Integer(toY));
		h.put("rotation", new Double(rotation));
	
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
		
		Lijn lijn = new Lijn(kleur, fromX, fromY, toX, toY);
		lijn.rotation = rotation;
		lijn.maakLijn();
		
		return lijn;
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

	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
	
	public boolean bbContains(int x, int y)
	{
		//int rx = inverseTransformX(x, y);
		//int ry = inverseTransformY(x, y);
		
		return bb.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		fromX += dx;
		fromY += dy;
		toX += dx; 
		toY += dy;
		cx += dx;
		cy += dy;
		
		fX += dx;
		fY += dy;
		tX += dx; 
		tY += dy;
		
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
	double rotation = 0;
	
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
		
		rechthoek.rotate(rotateStep, cx, cy);
		outerRechthoek.rotate(rotateStep, cx, cy);
		innerRechthoek.rotate(rotateStep, cx, cy);
		
		makeHandleBox();

	}
	
	public void scale(double scaleStep)
	{	
		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
		breedte = (int) Math.round(scaleStep * breedte);
		hoogte = (int) Math.round(scaleStep * hoogte);
		
		maakRechthoek();
		
		//rechthoek.scale(scaleStep, cx, cy);
		//outerRechthoek.scale(scaleStep, cx, cy);
		//innerRechthoek.scale(scaleStep, cx, cy);
		
		//makeHandleBox();
		
	}
	
	public void scale(double sx, double sy)
	{
		topLeftX = (int) Math.round(sx * topLeftX + (1 - sx) * cx);
		topLeftY = (int) Math.round(sy * topLeftY + (1 - sy) * cy);
		breedte = (int) Math.round(sx * breedte);
		hoogte = (int) Math.round(sy * hoogte);

		maakRechthoek();
		
//		rechthoek.scale(sx, sy, cx, cy);
//		outerRechthoek.scale(sx, sy, cx, cy);
//		innerRechthoek.scale(sx,sy, cx, cy);
		
//		makeHandleBox();
		
	}
	
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("topLeftX", new Integer(topLeftX));
		h.put("topLeftY", new Integer(topLeftY));
		h.put("breedte", new Integer(breedte));
		h.put("hoogte", new Integer(hoogte));
		h.put("rotation", new Double(rotation));
	
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
		
		Rechthoek rechthoek = new Rechthoek(kleur, topLeftX, topLeftY, breedte, hoogte);
		rechthoek.rotation = rotation;
		rechthoek.maakRechthoek();
		
		return rechthoek;
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
	
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
	
	public boolean bbContains(int x, int y)
	{
		
		return outerRechthoek.contains(x, y) && !innerRechthoek.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		topLeftX += dx;
		topLeftY += dy;
		cx += dx;
		cy += dy;
		
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
	double rotation = 0;
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

		ellips.rotate(rotateStep, cx, cy);
		outerEllips.rotate(rotateStep, cx, cy);
		innerEllips.rotate(rotateStep, cx, cy);

		makeHandleBox();

	}
	
	public void scale(double scaleStep)
	{	
		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
		breedte = (int) Math.round(scaleStep * breedte);
		hoogte = (int) Math.round(scaleStep * hoogte);
		
		makeEllips();
		
		makeHandleBox();
		
	}
	
	public void scale(double sx, double sy)
	{	
		topLeftX = (int) Math.round(sx * topLeftX + (1 - sx) * cx);
		topLeftY = (int) Math.round(sy * topLeftY + (1 - sy) * cy);
		breedte = (int) Math.round(sx * breedte);
		hoogte = (int) Math.round(sy * hoogte);
		
		makeEllips();
		
		makeHandleBox();
		
	}
	
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("topLeftX", new Integer(topLeftX));
		h.put("topLeftY", new Integer(topLeftY));
		h.put("breedte", new Integer(breedte));
		h.put("hoogte", new Integer(hoogte));
		h.put("rotation", new Double(rotation));
	
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
		
		Ellips ellips = new Ellips(kleur, topLeftX, topLeftY, breedte, hoogte);
		ellips.rotation = rotation;
		ellips.makeEllips();
		
		return ellips;
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
	
	public double inverseRotX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * x - Math.sin(- rotation) * y;
		
		return rotX;
		
	}
	
	public double inverseRotY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * x + Math.cos(- rotation) * y;
		
		return rotY;
	}
	
	public boolean bbContains(int x, int y)
	{
		
		return outerEllips.contains(x, y) && !innerEllips.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		topLeftX += dx;
		topLeftY += dy;
		
		ellips.translate(dx, dy);
		outerEllips.translate(dx, dy);
		innerEllips.translate(dx, dy);
		
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
	int bbFactor = 4;
	Rectangle bb;
	double cx, cy;
	
	Rectangle handleBox;
	int hbFactor = 4;
	
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;

	
	public TekstElement(CssColor c, String t, int x, int y)
	{
		kleur = c;
		tekst = new String(t);
		xPos = x;
		yPos = y;
				
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
		

		makeHandleBox();
	}

	public void makeHandleBox()
	{
		int minX = bb.x;
		int maxX = bb.x + bb.width;
		int minY = bb.y;
		int maxY = bb.y+bb.height;

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

//		if (KladjeGWTVeld.schalen)
//			makeScaleHandles();
		
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
	
	
	public void rotate(double rotateStep)
	{	
	}

	public void scale(double scaleStep)
	{	
	}
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("tekst", new String(tekst));
		h.put("xPos", new Integer(xPos));
		h.put("yPos", new Integer(yPos));
	
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
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("tekst"))
			tekst = h.getString("tekst");
		if (h.containsKey("xPos"))
			xPos = h.getInt("xPos");
		if (h.containsKey("yPos"))
			yPos = h.getInt("yPos");

		TekstElement tekstElement = new TekstElement(kleur, tekst, xPos, yPos);
		
		return tekstElement;
	}
	
	public void teken(Context2d g)
	{
		String fontString = "16px bold, sans-serif";
		g.setFont(fontString);
		
		TextMetrics tm = g.measureText(tekst);
		breedte = (int) Math.round(tm.getWidth());
		hoogte = 20;
		
		makeBB();
		
		//g.setStrokeStyle(kleur);
		//g.setFillStyle(kleur);
		g.setFillStyle(kleur);
		
		g.fillText(tekst, xPos, yPos + 15, breedte);
		
		
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

	}
	
	public void tekenBB(Context2d g)
	{
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.strokeRect(bb.x, bb.y, bb.width, bb.height);
		
		g.setLineWidth(1.5d);

	}

	public boolean bbContains(int x, int y)
	{
		return bb.contains(x, y);
	}

	public void translate(int dx, int dy)
	{
		xPos += dx;
		yPos += dy;
		bb.translate(dx, dy);
		cx += dx;
		cy += dy;
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;

		boolean isContainedIn = r.contains(bb.x, bb.y) && r.contains(bb.x + bb.width, bb.y + bb.height);
		
		return isContainedIn;
	}
	
}