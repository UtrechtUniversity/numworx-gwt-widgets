package fi.kladjegwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.writemathgwt.client.engine.DoubleRectangle;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class KStrokeContainer {

	private KladjeGWTVeld parent;
	private StrokeContainer strokeContainer;
	
	private boolean active = false;
	private double activeTranslation;
	
	private CssColor drawingColor = CssColor.make(80, 80, 80);
	private Rectangle box, writeBox;
	private boolean correct = false;
	private boolean isfalse = false;
	private boolean checkable;
	
	public KStrokeContainer (KladjeGWTVeld parent) {
		this.parent = parent;
		strokeContainer = new StrokeContainer();
	}
	
	public void addStroke(Stroke stroke) {
		strokeContainer.addStroke(stroke);
		box = null;
		writeBox = null;
	}
	
	public void clear() {
		strokeContainer.getStrokes().clear();
	}
	
	public Rectangle getCloseButtonArea() {
		int x = getWriteBox().x + getWriteBox().width - 30; 
		int y = getWriteBox().y; 
		return new Rectangle(x,y,30,30);
	}
	
	public Rectangle getCheckButtonArea() {
		int x = getWriteBox().x + getWriteBox().width - 77; 
		int y = getWriteBox().y; 
		return new Rectangle(x,y,40,40);
	}
	
	private void drawcloseButton(Context2d g, Rectangle r) {
		int m = 10;
		
		g.setStrokeStyle( CssColor.make(38, 115, 182));
		g.setLineWidth(4.0d);
		g.beginPath();
		g.moveTo(r.x+m, r.y);
		g.lineTo(r.x+m, r.y+m);
		g.lineTo(r.x, r.y+m);
		g.stroke();
		
		g.moveTo(r.x+r.width-m, r.y);
		g.lineTo(r.x+r.width-m, r.y+m);
		g.lineTo(r.x+r.width, r.y+m);
		g.stroke();
		
		g.moveTo(r.x, r.y+r.height-m);
		g.lineTo(r.x+m,  r.y+r.height-m);
		g.lineTo(r.x+m, r.y+r.height);
		g.stroke();
		
		g.moveTo(r.x+r.width, r.y+r.height-m);
		g.lineTo(r.x+r.width-m,  r.y+r.height-m);
		g.lineTo(r.x+r.width-m, r.y+r.height);
		g.stroke();
	}
	
	private void drawShadow(Context2d g, Rectangle r) {
		for(int i=0 ; i<10 ; i++) {
			g.setStrokeStyle( CssColor.make("rgba("+(150+10*i)+","+(150+10*i)+","+(150+10*i)+","+(1-0.1*i)+")"));
			g.setLineWidth(2.0d);
			g.beginPath();
			g.rect(r.x-2*i, r.y-2*i, r.width+4*i, r.height+4*i);
			g.closePath();
			g.stroke();
		}
	}
	
	private void drawGrid (Context2d g, Rectangle r) {
		CssColor ruitjesKleur = CssColor.make(38, 115, 182);
				
					int	lineDistance = 30;
					g.setStrokeStyle(ruitjesKleur);
					g.setLineWidth(0.2d);
					int vSteps = r.height / lineDistance;
					for (int vCnt = 1; vCnt <= vSteps; vCnt++)
					{
						g.beginPath();
						g.moveTo(r.x, r.y + vCnt * lineDistance);
						g.lineTo(r.x + r.width - 1, r.y + vCnt * lineDistance);
						g.stroke();
					}
					int hSteps = r.width / lineDistance;
					for (int hCnt = 1; hCnt <= hSteps; hCnt++)
					{
						g.beginPath();
						g.moveTo(r.x + hCnt * lineDistance, r.y);
						g.lineTo(r.x + hCnt * lineDistance, r.y + r.height - 1);
						g.stroke();
					}
					
					
	}
	
	public void draw(Context2d g) {
		if(strokeContainer.getStrokes().size()>0) {
			if(active) {
				g.setFillStyle(CssColor.make(255, 255, 255));
				g.fillRect(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10);
				drawShadow(g,new Rectangle(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10));
				drawGrid(g,new Rectangle(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10));
				
				g.setStrokeStyle(CssColor.make(80, 80, 80));
				//g.setFillStyle(CssColor.make(239, 241, 243));
				g.fillRect(getWriteBox().x + getWriteBox().width-40, getWriteBox().y, 40, 40);

				drawcloseButton(g, new Rectangle(getWriteBox().x + getWriteBox().width-37, getWriteBox().y+3, 34, 34));
				
//				g.fillRect(getWriteBox().x + getWriteBox().width-77, getWriteBox().y+3, 34, 34);
//				g.setStrokeStyle(CssColor.make(0, 200, 0));
//				g.setLineWidth(5.0d);
//				g.beginPath();
//				g.moveTo(getWriteBox().x + getWriteBox().width-70, getWriteBox().y+10);
//				g.lineTo(getWriteBox().x + getWriteBox().width-60, getWriteBox().y+30);
//				g.lineTo(getWriteBox().x + getWriteBox().width-50, getWriteBox().y+10);
//				g.moveTo(getWriteBox().x + getWriteBox().width-70, getWriteBox().y+10);
//				g.closePath();
//				g.stroke();
				
				g.setStrokeStyle(drawingColor);
				g.setLineWidth(3.0d);
				
				if(correct||isfalse) {
					//g.setFillStyle(CssColor.make(240, 255, 240));
					if(correct)
						g.setFillStyle(CssColor.make(0, 200, 0));
					if(isfalse)
						g.setFillStyle(CssColor.make(200, 0, 0));
					g.beginPath();
					g.arc(getWriteBox().x + 20, getWriteBox().y + 20 , 8, 0, 8* Math.PI);
					g.closePath();
					g.stroke();
					g.fill();
				}
				
			}
			else {
				//g.setFillStyle(CssColor.make(243, 241, 239));
				g.setFillStyle(CssColor.make(255, 255, 255));
				g.setLineWidth(1.5d);
				if(correct||isfalse) {
					//g.setFillStyle(CssColor.make(240, 255, 240));
					//g.fillRect(getBox().x-30, getBox().y-5, getBox().width+35, getBox().height+10);
					if(correct)
						g.setFillStyle(CssColor.make(0, 200, 0));
					if(isfalse)
						g.setFillStyle(CssColor.make(200, 0, 0));
					g.beginPath();
					g.arc(getBox().x-20, getBox().y+getBox().height/2 , 5, 0, 5* Math.PI);
					g.closePath();
					g.stroke();
					g.fill();
				}
				//else
					//g.fillRect(getBox().x-5, getBox().y-5, getBox().width+10, getBox().height+10);
			}	
		}
		
		
		
		//g.setStrokeStyle(drawingColor);
		ArrayList<Stroke> strokes = strokeContainer.getStrokes();
		for(int i = 0 ; i < strokes.size() ; i++) {
			Stroke stroke = strokes.get(i);
			g.beginPath();
			double x0 = (int)stroke.getParsePoints().get(0).x;
			double y0 = (int)stroke.getParsePoints().get(0).y;
			g.moveTo(x0, y0);
			if(stroke.getParsePointsbox().width>3 ||  stroke.getParsePointsbox().height>3) {
				for(int j = 1 ; j < stroke.getParsePoints().size() ; j++) {
					double x = stroke.getParsePoints().get(j).x ;
					double y = stroke.getParsePoints().get(j).y;
					g.lineTo(x, y);
				}
				g.moveTo(x0, y0);
				g.closePath();
				g.stroke();
			}
			else {
				g.arc(x0, y0, 1.5, 0, 1.5* Math.PI);
				g.closePath();
				g.stroke();
			}
		}
		g.setLineWidth(3.0d);
	}
	
	public void setCorrect(boolean correct) {
		this.correct = correct;
		if(correct)
			isfalse = false;
	}
	
	public void setFalse(boolean isfalse) {
		this.isfalse = isfalse;
		if(isfalse)
			correct = false;
	}
	
	public void setActive (boolean b) {
		active = b;
		if(active && getBox()!=null) {
			activeTranslation = getBox().x - 40;
			translate((int)-activeTranslation,0);
		}
		else if(getBox()!=null)
			translate((int)activeTranslation,0);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getFormulaString() {
		return strokeContainer.getFormulaString();
	}
	
	public DoubleRectangle getBoundingBox() {
		return strokeContainer.getBoundingBox();
	}
	
	public Rectangle getBox() {
		if(box == null && strokeContainer != null && strokeContainer.getBoundingBox()!=null) {
			int x = (int)strokeContainer.getBoundingBox().x;
			int y = (int)strokeContainer.getBoundingBox().y;
			int width = (int)strokeContainer.getBoundingBox().width;
			int height = (int)strokeContainer.getBoundingBox().height;
			box = new Rectangle(x, y, width, height);
		}
		return box;
	}
	
	public Rectangle getWriteBox() {
		int margin = 50;
		if(writeBox==null && strokeContainer != null && strokeContainer.getBoundingBox()!=null) {
//			int x = (int)strokeContainer.getBoundingBox().x;
//			int y = (int)strokeContainer.getBoundingBox().y;
//			int width = (int)strokeContainer.getBoundingBox().width;
//			int height = (int)strokeContainer.getBoundingBox().height;
			int x = 20;
			int y = (int)Math.max(25,strokeContainer.getBoundingBox().y - margin);
			y= (int)Math.min(y, parent.hoogte - strokeContainer.getBoundingBox().height-2*margin-25);
			int width = parent.breedte-40;
			int height = (int)strokeContainer.getBoundingBox().height + 2*margin;
			writeBox = new Rectangle(x, y, width, height);
		}
		return writeBox;
	}
	
	public boolean contains(int x, int y) {
		if(getBox()==null)
			return false;
		return getBox().contains(x, y);
	}
	
	public boolean writeBoxContains(int x, int y) {
		if(getWriteBox()==null)
			return false;
		return getWriteBox().contains(x, y);
	}
	
	public boolean isNotRelevant() {
		if(strokeContainer.getStrokes().size()==0 || strokeContainer.getDiagonal()<7)
			return true;
		return false;
	}
	
	public boolean contains(int x, int y, int margin) {
		if(strokeContainer != null && strokeContainer.getBoundingBox()!=null) {
			int xb = (int)strokeContainer.getBoundingBox().x;
			int yb = (int)strokeContainer.getBoundingBox().y;
			int width = (int)strokeContainer.getBoundingBox().width;
			int height = (int)strokeContainer.getBoundingBox().height;
			Rectangle box = new Rectangle(xb-margin, yb-margin, width+2*margin, height+2*margin);
			if(box.contains(x,y))
				return true;
		}
		return false;
	}
	
	public void translate(int dx, int dy) {
		strokeContainer.translate(dx, dy);
		box = null;
		writeBox = null;
	}
	
	public void scale(double factor) {
		strokeContainer.scale(factor);
		box = null;
		writeBox = null;
	}
	
	public void scale(double cx, double cy,double factor) {
		strokeContainer.scale(cx, cy, factor);
		box = null;
		writeBox = null;
	}
	
	public double getDiagonal() {
		return strokeContainer.getDiagonal();
	}
	
	public HashMap<String,Object> getState() {
		HashMap<String,Object> map = strokeContainer.getState();
		map.put("correct", new Boolean(correct));
		map.put("isfalse", new Boolean(isfalse));
		return map;
	}
	
	public void setState (Map<String,Object> map) {
		strokeContainer.setState(map);
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		if(launchState.containsKey("correct"))
			correct = launchState.getBoolean("correct");
		if(launchState.containsKey("isfalse"))
			isfalse = launchState.getBoolean("isfalse");
	}
}
