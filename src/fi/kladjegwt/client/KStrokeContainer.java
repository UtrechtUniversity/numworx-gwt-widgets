package fi.kladjegwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.writemathgwt.client.engine.DoubleRectangle;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;

public class KStrokeContainer {

	private KladjeGWTVeld parent;
	private StrokeContainer strokeContainer;
	
	private boolean active = false;
	
	private double activeTranslation;
	
	private CssColor drawingColor = CssColor.make(80, 80, 80);
	private Rectangle box, writeBox;
	
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
	
	public void draw(Context2d g) {
		if(strokeContainer.getStrokes().size()>0) {
			if(active) {
				g.setFillStyle(CssColor.make(255, 255, 255));
				
				g.fillRect(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10);
				g.setLineWidth(1.2d);
				g.beginPath();
				g.moveTo(getWriteBox().x + getWriteBox().width-30, getWriteBox().y+10);
				g.lineTo(getWriteBox().x + getWriteBox().width-10, getWriteBox().y+30);
				g.closePath();
				g.stroke();
				g.beginPath();
				g.moveTo(getWriteBox().x + getWriteBox().width-30, getWriteBox().y+30);
				g.lineTo(getWriteBox().x + getWriteBox().width-10, getWriteBox().y+10);
				g.closePath();
				g.stroke();
				g.setLineWidth(3.0d);
			}
			else {
				g.setFillStyle(CssColor.make(243, 241, 239));
				g.setLineWidth(1.5d);
				g.fillRect(getBox().x-5, getBox().y-5, getBox().width+10, getBox().height+10);
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
			int y = (int)Math.max(5,strokeContainer.getBoundingBox().y - margin);
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
		return strokeContainer.getState();
	}
	
	public void setState (Map<String,Object> map) {
		strokeContainer.setState(map);
	}
}
