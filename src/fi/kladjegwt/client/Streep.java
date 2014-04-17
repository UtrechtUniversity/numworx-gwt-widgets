package fi.kladjegwt.client;

//import java.awt.Polygon;
import java.util.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Streep 
{
	CssColor kleur;
	int[] puntenX, puntenY;
	int bbFactor = 4;
	Polygon streep;
	Polygon bb;
	Polygon streepBB;
	double cx = 0, cy = 0;
	double rotation = 0;
	
	public Streep(CssColor c, Vector<Point> punten)
	{	kleur = c;
		puntenX = new int[punten.size()];
		puntenY = new int[punten.size()];
		for (int pCnt = 0; pCnt < punten.size(); pCnt++)
		{	Point pt = (Point) punten.elementAt(pCnt);
			puntenX[pCnt] = pt.x;
			puntenY[pCnt] = pt.y;
			
			cx += puntenX[pCnt];
			cy += puntenY[pCnt];
		}
		
		cx /= puntenX.length;
		cy /= puntenY.length;
		
		maakBB();
	}
	
	public Streep(CssColor c, int[] ptX, int[] ptY)
	{	kleur = c;
		puntenX = ptX;
		puntenY = ptY;
// Wim: bereken center of mass		
		for(int pCnt = 0; pCnt < puntenX.length; pCnt ++)
		{
			cx += puntenX[pCnt];
			cy += puntenY[pCnt];
		}
		cx /= puntenX.length;
		cy /= puntenY.length;
		
		maakBB();
	}

	public void maakStreep()
	{
		streep = new Polygon();
		for (int pCnt = 0; pCnt < puntenX.length; pCnt++)
		{	streep.addPoint(puntenX[pCnt], puntenY[pCnt]);
		}
	}
	
	public void maakBB()
	{
		bb = new Polygon();
		
		if (puntenX.length == 1)
		{	bb.addPoint(puntenX[0] - bbFactor, puntenY[0] - bbFactor);
			bb.addPoint(puntenX[0] + bbFactor, puntenY[0] - bbFactor);
			bb.addPoint(puntenX[0] + bbFactor, puntenY[0] + bbFactor);
			bb.addPoint(puntenX[0] - bbFactor, puntenY[0] + bbFactor);
		}
		if (puntenX.length > 1)
		{  
			
			// for loop 1
			for (int pCnt = 1; pCnt < puntenX.length; pCnt++)
			{	
				int fromX = puntenX[pCnt - 1];
				int fromY = puntenY[pCnt - 1];
				int toX = puntenX[pCnt];
				int toY = puntenY[pCnt];
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
						bb.addPoint(px, py); 
						// 	tweede punt
						px = toX + nx * bbFactor / nl;
						py = toY + ny * bbFactor / nl;
						if (normal1)
							bb.addPoint(px, py);
						}
					else
					{	
//					 	vierde punt
						double px = fromX - nx * bbFactor / nl;
						double py = fromY - ny * bbFactor / nl;
						bb.addPoint(px, py);
						
						//derde punt
						px = toX - nx * bbFactor / nl;
						py = toY - ny * bbFactor / nl;
						bb.addPoint(px, py);

						// 	vierde punt
						px = fromX - nx * bbFactor / nl;
						py = fromY - ny * bbFactor / nl;
						bb.addPoint(px, py);
					}	
				}	
		
			}
		
			// for loop 2
			for (int pCnt = puntenX.length - 1; pCnt > 0; pCnt--)
			{	
				int fromX = puntenX[pCnt - 1];
				int fromY = puntenY[pCnt - 1];
				int toX = puntenX[pCnt];
				int toY = puntenY[pCnt];
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
						bb.addPoint(px, py);
					
						// eerste punt
						px = fromX + nx * bbFactor / nl;
						py = fromY + ny * bbFactor / nl;
						bb.addPoint(px, py);
					}
					else
					{	
						
						// derde punt
						double px = toX - nx * bbFactor / nl;
						double py = toY - ny * bbFactor / nl;
						bb.addPoint(px, py);
						// 	vierde punt
						px = fromX - nx * bbFactor / nl;
						py = fromY - ny * bbFactor / nl;
						bb.addPoint(px, py);
					}
				}	
		
			}
		}
	}

	public void maakStreepBB()
	{
		streepBB = new Polygon(bb);
	}
	public void rotate(double rotateStep)
	{	rotation += rotateStep;
	}
	
	public void scale(double scaleStep)
	{
		for (int pCnt = 0; pCnt < puntenX.length; pCnt++)
		{	
			puntenX[pCnt] = (int) Math.round(scaleStep * puntenX[pCnt] + (1 - scaleStep) * cx);
			puntenY[pCnt] = (int) Math.round(scaleStep * puntenY[pCnt] + (1 - scaleStep) * cy);
		}
		
		maakBB();
		
	}
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		
		ArrayList<Integer> puntenXAL = new ArrayList<Integer>();
		ArrayList<Integer> puntenYAL = new ArrayList<Integer>();
		for (int pCnt = 0; pCnt < puntenX.length; pCnt++)
		{
			puntenXAL.add(new Integer(puntenX[pCnt]));
			puntenYAL.add(new Integer(puntenY[pCnt]));
		}

		h.put("puntenX", puntenXAL);
		h.put("puntenY", puntenYAL);
		
		h.put("rotation", new Double(rotation));
	
		return h;
	}
	
	public static Streep setState(HashMap<String, Object> h)
	{
		CssColor kleur = KladjeGWTVeld.zwart;
		int[] puntenX = new int[0];
		int[] puntenY = new int[0];
		List<?> puntenXAL = new ArrayList<Integer>();
		List<?> puntenYAL = new ArrayList<Integer>();
		double rotation = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.get("kleurgwt").toString());
		
		// data from getState
		//if (h.containsKey("puntenXAL"))
		//	puntenXAL = (ArrayList<Integer>) h.get("puntenXAL");
		//if (h.containsKey("puntenYAL"))
		//	puntenYAL = (ArrayList<Integer>) h.get("puntenYAL");
		
		// launchdata or data from getState
		if (h.containsKey("puntenX"))
			puntenXAL = KladjeGWTVeld.toArrayList(h.get("puntenX") );
		if (h.containsKey("puntenY"))
			puntenYAL = KladjeGWTVeld.toArrayList(h.get("puntenY") );

		if (h.containsKey("rotation"))
			rotation = ((Number) h.get("rotation")).doubleValue();

		puntenX = new int[puntenXAL.size()];
		puntenY = new int[puntenYAL.size()];
		for (int pCnt = 0; pCnt < puntenXAL.size(); pCnt++)
		{
			puntenX[pCnt] = ((Number) puntenXAL.get(pCnt)).intValue();
			puntenY[pCnt] = ((Number) puntenYAL.get(pCnt)).intValue();
		}
		
		
		Streep streep = new Streep(kleur, puntenX, puntenY);
		streep.rotation = rotation;
		
		return streep;
	}
	
	
	public void teken(Context2d g)
	{
		maakStreep();
		streep.rotate(rotation, cx, cy);
		
		g.setStrokeStyle(kleur);		
		
		if (puntenX.length == 1)
		{	//g.drawLine(puntenX[0], puntenY[0], puntenX[0], puntenY[0]);
			g.beginPath();
			g.strokeRect(puntenX[0], puntenY[0], 1, 1);
		}
		if (puntenX.length > 1)
		{	
			g.beginPath();
			g.moveTo(streep.doubleX[0], streep.doubleY[0]);
			for (int pCnt = 1; pCnt < streep.aantalPunten; pCnt++)
			{	g.lineTo(streep.doubleX[pCnt], streep.doubleY[pCnt]);
			}
			g.stroke();
			
		}
		
	}
	
	public void tekenBB(Context2d g)
	{
		maakStreepBB();
		streepBB.rotate(rotation, cx, cy);
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.moveTo(streepBB.doubleX[0], streepBB.doubleY[0]);
		for (int k = 1; k < streepBB.aantalPunten; k++) 
		{	g.lineTo(streepBB.doubleX[k], streepBB.doubleY[k]);
		}
		g.lineTo(streepBB.doubleX[0], streepBB.doubleY[0]);
		g.closePath();
		g.stroke();

			
		g.setLineWidth(1.5d);
	}

	public int inverseTransformX(int x, int y)
	{
		double rotX = Math.cos(-rotation) * (x - cx) - Math.sin(- rotation) * (y - cy);
		int rx = (int) Math.round(cx + rotX);
		
		return rx;
	}

	public int inverseTransformY(int x, int y)
	{
		double rotY = Math.sin(-rotation) * (x - cx) + Math.cos(- rotation) * (y - cy);
		int ry = (int) Math.round(cy + rotY);
		
		return ry;
	}
	
	public boolean bbContains(int x, int y)
	{
		int rx = inverseTransformX(x, y);
		int ry = inverseTransformY(x, y);

		return bb.contains(rx, ry);
	}
	
	public void translate(int dx, int dy)
	{
		for (int pCnt = 0; pCnt < puntenX.length; pCnt++)
		{	puntenX[pCnt] += dx;
			puntenY[pCnt] += dy;
		}

		cx += dx;
		cy += dy;

		bb.translate(dx, dy);
	}
	
	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;		
		
		boolean isContainedIn = true;
		
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			int bbX = inverseTransformX(bb.puntenX[pCnt], bb.puntenY[pCnt]);
			int bbY = inverseTransformY(bb.puntenX[pCnt], bb.puntenY[pCnt]);
			
			isContainedIn = isContainedIn && r.contains(bbX, bbY);
			//isContainedIn = isContainedIn && r.contains(bb.xpoints[pCnt], bb.ypoints[pCnt]);
		}
		
		return isContainedIn;
	}
}
class Lijn
{
	CssColor kleur;
	int fromX, fromY, toX, toY;
	int bbFactor = 4;
	Polygon lijn; 
	Polygon bb;
	Polygon lijnBB;
	double cx, cy;
	double rotation = 0;
	
	public Lijn(CssColor c, int fromX, int fromY, int toX, int toY)
	{
		kleur = c;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;

		cx = ((double) fromX + (double) toX) / 2;
		cy = ((double) fromY + (double) toY) / 2;
		
		makeBB();
	}
	
	public void makeLijn()
	{
		lijn = new Polygon();
		lijn.addPoint(fromX, fromY);
		lijn.addPoint(toX, toY);
	}
	
	public void makeLijnBB()
	{
		lijnBB = new Polygon(bb);
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
	}
	
	public void scale(double scaleStep)
	{	
		fromX = (int) Math.round(scaleStep * fromX + (1 - scaleStep) * cx);
		fromY = (int) Math.round(scaleStep * fromY + (1 - scaleStep) * cy);
		toX = (int) Math.round(scaleStep * toX + (1 - scaleStep) * cx);
		toY = (int) Math.round(scaleStep * toY + (1 - scaleStep) * cy);
		
		makeBB();
		
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
	
	public static Lijn setState(HashMap<String, Object> h)
	{
		CssColor kleur = KladjeGWTVeld.zwart;
		int fromX = 0; 
		int fromY = 0;
		int toX = 0;
		int toY = 0;
		double rotation = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.get("kleurgwt").toString());
		if (h.containsKey("fromX"))
			fromX = ((Number) h.get("fromX")).intValue();
		if (h.containsKey("fromY"))
			fromY = ((Number) h.get("fromY")).intValue();

		if (h.containsKey("toX"))
			toX = ((Number) h.get("toX")).intValue();
		if (h.containsKey("toY"))
			toY = ((Number) h.get("toY")).intValue();

		if (h.containsKey("rotation"))
			rotation = ((Number) h.get("rotation")).doubleValue();
		
		Lijn lijn = new Lijn(kleur, fromX, fromY, toX, toY);
		lijn.rotation = rotation;
		
		return lijn;
	}
	
	public void teken(Context2d g)
	{
		makeLijn();
		lijn.rotate(rotation, cx, cy);
		
		g.setStrokeStyle(kleur);
		
		g.beginPath();
		g.moveTo(lijn.doubleX[0], lijn.doubleY[0]);
		g.lineTo(lijn.doubleX[1], lijn.doubleY[1]);
		g.stroke();

	}

	public void tekenBB(Context2d g)
	{
		makeLijnBB();
		lijnBB.rotate(rotation, cx, cy);
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();		
		g.moveTo(lijnBB.doubleX[0], lijnBB.doubleY[0]);
		for (int k = 1; k < lijnBB.aantalPunten; k++) 
		{	g.lineTo(lijnBB.doubleX[k], lijnBB.doubleY[k]);
		}
		g.lineTo(lijnBB.doubleX[0], lijnBB.doubleY[0]);
		g.closePath();
		g.stroke();

				
		g.setLineWidth(1.5d);
		
	}

	public int inverseTransformX(int x, int y)
	{
		double rotX = Math.cos(-rotation) * (x - cx) - Math.sin(- rotation) * (y - cy);
		int rx = (int) Math.round(cx + rotX);
		
		return rx;
	}

	public int inverseTransformY(int x, int y)
	{
		double rotY = Math.sin(-rotation) * (x - cx) + Math.cos(- rotation) * (y - cy);
		int ry = (int) Math.round(cy + rotY);
		
		return ry;
	}
	
	public boolean bbContains(int x, int y)
	{
		int rx = inverseTransformX(x, y);
		int ry = inverseTransformY(x, y);
		
		return bb.contains(rx, ry);
	}

	public void translate(int dx, int dy)
	{
		fromX += dx;
		fromY += dy;
		toX += dx; 
		toY += dy;
		cx += dx;
		cy += dy;
		
		bb.translate(dx, dy);
		
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;
		
		boolean isContainedIn = true;
		
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			int bbX = inverseTransformX(bb.puntenX[pCnt], bb.puntenY[pCnt]);
			int bbY = inverseTransformY(bb.puntenX[pCnt], bb.puntenY[pCnt]);
			
			isContainedIn = isContainedIn && r.contains(bbX, bbY);
			//isContainedIn = isContainedIn && r.contains(bb.xpoints[pCnt], bb.ypoints[pCnt]);
		}
		
		return isContainedIn;
	}	
}
class Rechthoek
{	CssColor kleur;
	int topLeftX, topLeftY, breedte, hoogte;
	int bbFactor = 4;
	Polygon rechthoek;
	Rectangle outerBB;
	Rectangle innerBB;
	Polygon outerRechthoek;
	Polygon innerRechthoek;
	double cx, cy;
	double rotation = 0;
	
	public Rechthoek(CssColor c, int x, int y, int w, int h)
	{
		kleur = c;
		topLeftX = x;
		topLeftY = y;
		breedte = w;
		hoogte = h;
		
		cx = topLeftX + ((double) breedte) / 2;
		cy = topLeftY + ((double) hoogte) / 2;
		
		makeBB();
		
	}

	public void makeRechthoek()
	{
		rechthoek = new Polygon();
		rechthoek.addPoint(topLeftX, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY + hoogte);
		rechthoek.addPoint(topLeftX, topLeftY + hoogte);
		
	}
	
	public void makeBB()
	{
		outerBB = new Rectangle(topLeftX - bbFactor, topLeftY - bbFactor, 
			    breedte + 2 * bbFactor, hoogte + 2 * bbFactor);
		innerBB = new Rectangle(topLeftX + bbFactor, topLeftY + bbFactor, 
			    breedte - 2 * bbFactor, hoogte - 2 * bbFactor);
		
	}
	
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
	
	public void rotate(double rotateStep)
	{	rotation += rotateStep;
		rechthoek.rotate(rotateStep, cx, cy);
		
	}
	
	public void scale(double scaleStep)
	{	
		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
		breedte = (int) Math.round(scaleStep * breedte);
		hoogte = (int) Math.round(scaleStep * hoogte);
		
		makeBB();
		
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
	
	public static Rechthoek setState(HashMap<String, Object> h)
	{
		CssColor kleur = KladjeGWTVeld.zwart;
		int topLeftX = 0; 
		int topLeftY = 0;
		int breedte = 0;
		int hoogte = 0;
		double rotation = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.get("kleurgwt").toString());
		if (h.containsKey("topLeftX"))
			topLeftX = ((Number) h.get("topLeftX")).intValue();
		if (h.containsKey("topLeftY"))
			topLeftY = ((Number) h.get("topLeftY")).intValue();

		if (h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		
		if (h.containsKey("rotation"))
			rotation = ((Number) h.get("rotation")).doubleValue();
		
		Rechthoek rechthoek = new Rechthoek(kleur, topLeftX, topLeftY, breedte, hoogte);
		rechthoek.rotation = rotation;
		
		return rechthoek;
	}
	
	public void teken(Context2d g)
	{
		makeRechthoek();
		rechthoek.rotate(rotation, cx, cy);

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

	public void tekenBB(Context2d g)
	{

		makeOuterInner();
		outerRechthoek.rotate(rotation, cx, cy);
		innerRechthoek.rotate(rotation, cx, cy);
		
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
	public int transformX(double x, double y)
	{
		double rotX = Math.cos(rotation) * (x - cx) - Math.sin(rotation) * (y - cy);
		int rx = (int) Math.round(cx + rotX);
		
		return rx;
	}
*/
/*	
	public int transformY(double x, double y)
	{
		double rotY = Math.sin(rotation) * (x - cx) + Math.cos(rotation) * (y - cy);
		int ry = (int) Math.round(cy + rotY);
		
		return ry;
	}
*/	
	public int inverseTransformX(double x, double y)
	{
		double rotX = Math.cos(-rotation) * (x - cx) - Math.sin(- rotation) * (y - cy);
		int rx = (int) Math.round(cx + rotX);
		
		return rx;
	}

	public int inverseTransformY(double x, double y)
	{
		double rotY = Math.sin(-rotation) * (x - cx) + Math.cos(- rotation) * (y - cy);
		int ry = (int) Math.round(cy + rotY);
		
		return ry;
	}
	
	public boolean bbContains(int x, int y)
	{
		int rx = inverseTransformX(x, y);
		int ry = inverseTransformY(x, y);
		
		return outerBB.contains(rx, ry) && !innerBB.contains(rx, ry);
	}

	public void translate(int dx, int dy)
	{
		topLeftX += dx;
		topLeftY += dy;
		outerBB.translate(dx, dy);
		innerBB.translate(dx, dy);
		cx += dx;
		cy += dy;
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;

		int toBBx = inverseTransformX(outerBB.x, outerBB.y);
		int toBBy = inverseTransformY(outerBB.x, outerBB.y);
		int toBBx2 = inverseTransformX(outerBB.x + outerBB.width, outerBB.y + outerBB.height);
		int toBBy2 = inverseTransformY(outerBB.x + outerBB.width, outerBB.y + outerBB.height);
		
		boolean isContainedIn = r.contains(toBBx, toBBy) &&	r.contains(toBBx2, toBBy2);		
		
		return isContainedIn;
	}
	
}
class Ellips
{	CssColor kleur;
	int topLeftX, topLeftY, breedte, hoogte;
	int bbFactor = 4;
	Polygon ellips;
	Rectangle outerBB, innerBB;
	Polygon outerEllips, innerEllips;
	double cx, cy;
	double rotation = 0;
	int steps = 50;
	
	public Ellips(CssColor c, int x, int y, int w, int h)
	{
		kleur = c;
		topLeftX = x;
		topLeftY = y;
		breedte = w;
		hoogte = h;
		
		cx = topLeftX + ((double) breedte) / 2;
		cy = topLeftY + ((double) hoogte) / 2;
		
		makeBB();
		
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
		
	}
	
	public void makeBB()
	{
		outerBB = new Rectangle(topLeftX - bbFactor, topLeftY - bbFactor, 
			    breedte + 2 * bbFactor, hoogte + 2 * bbFactor);
		innerBB = new Rectangle(topLeftX + bbFactor, topLeftY + bbFactor, 
			    breedte - 2 * bbFactor, hoogte - 2 * bbFactor);
		
	}

	public void makeOuterInner()
	{
		double angleStep = 2 * Math.PI / steps;
		
		outerEllips = new Polygon();
		for (int pCnt = 0; pCnt <= steps; pCnt++)
		{
			double x = cx + (outerBB.width / 2) * Math.cos(pCnt * angleStep);
			double y = cy - (outerBB.height / 2) * Math.sin(pCnt * angleStep);
			outerEllips.addPoint(x, y);
		}

		innerEllips = new Polygon();
		for (int pCnt = 0; pCnt <= steps; pCnt++)
		{
			double x = cx + (innerBB.width / 2) * Math.cos(pCnt * angleStep);
			double y = cy - (innerBB.height / 2) * Math.sin(pCnt * angleStep);
			innerEllips.addPoint(x, y);
		}
		
	}
	
	public void rotate(double rotateStep)
	{	rotation += rotateStep;
	}
	
	public void scale(double scaleStep)
	{	
		topLeftX = (int) Math.round(scaleStep * topLeftX + (1 - scaleStep) * cx);
		topLeftY = (int) Math.round(scaleStep * topLeftY + (1 - scaleStep) * cy);
		breedte = (int) Math.round(scaleStep * breedte);
		hoogte = (int) Math.round(scaleStep * hoogte);
		
		makeBB();
		
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
	
	public static Ellips setState(HashMap<String, Object> h)
	{
		CssColor kleur = KladjeGWTVeld.zwart;
		int topLeftX = 0; 
		int topLeftY = 0;
		int breedte = 0;
		int hoogte = 0;
		double rotation = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make( h.get("kleurgwt").toString());
		if (h.containsKey("topLeftX"))
			topLeftX = ((Number) h.get("topLeftX")).intValue();
		if (h.containsKey("topLeftY"))
			topLeftY = ((Number) h.get("topLeftY")).intValue();

		if (h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();

		if (h.containsKey("rotation"))
			rotation = ((Number) h.get("rotation")).doubleValue();
		
		Ellips ellips = new Ellips(kleur, topLeftX, topLeftY, breedte, hoogte);
		ellips.rotation = rotation;
		
		return ellips;
	}

	public void teken(Context2d g)
	{
		makeEllips();
		ellips.rotate(rotation, cx, cy);
		
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
	
	public void tekenBB(Context2d g)
	{
		makeOuterInner();
		outerEllips.rotate(rotation, cx, cy);
		innerEllips.rotate(rotation, cx, cy);
		
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

	public int inverseTransformX(int x, int y)
	{
		double rotX = Math.cos(-rotation) * (x - cx) - Math.sin(- rotation) * (y - cy);
		int rx = (int) Math.round(cx + rotX);
		
		return rx;
	}

	public int inverseTransformY(int x, int y)
	{
		double rotY = Math.sin(-rotation) * (x - cx) + Math.cos(- rotation) * (y - cy);
		int ry = (int) Math.round(cy + rotY);
		
		return ry;
	}
	
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
	
	public boolean bbContains(int x, int y)
	{
		boolean outer = ellipsContains(x, y, outerBB);
		boolean inner = ellipsContains(x, y, innerBB);
		
		return outer && !inner;
	}

	public void translate(int dx, int dy)
	{
		topLeftX += dx;
		topLeftY += dy;
		outerBB.translate(dx, dy);
		innerBB.translate(dx, dy);
		cx += dx;
		cy += dy;
		
	}	

	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;
		
		int toBBx = inverseTransformX(outerBB.x, outerBB.y);
		int toBBy = inverseTransformY(outerBB.x, outerBB.y);
		int toBBx2 = inverseTransformX(outerBB.x + outerBB.width, outerBB.y + outerBB.height);
		int toBBy2 = inverseTransformY(outerBB.x + outerBB.width, outerBB.y + outerBB.height);
		
		boolean isContainedIn = r.contains(toBBx, toBBy) &&	r.contains(toBBx2, toBBy2);		
		
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

	public static TekstElement setState(HashMap<String, Object> h)
	{
		CssColor kleur = KladjeGWTVeld.zwart;
		String tekst = new String("");
		int xPos = 0;
		int yPos = 0;
		double rotation = 0;
		double scaleX = 1;
		double scaleY = 1;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make( h.get("kleurgwt").toString() );
		if (h.containsKey("tekst"))
			tekst = (String) h.get("tekst");
		if (h.containsKey("xPos"))
			xPos = ((Number) h.get("xPos")).intValue();
		if (h.containsKey("yPos"))
			yPos = ((Number) h.get("yPos")).intValue();

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