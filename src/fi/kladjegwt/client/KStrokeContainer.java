package fi.kladjegwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.writemathgwt.client.engine.DoubleRectangle;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class KStrokeContainer {

	private KladjeGWTVeld parent;
	private StrokeContainer strokeContainer;
	
	private boolean active = false;
	private boolean popupMode = false;
	private double activeTranslationX;
	private double activeTranslationY;
	private double correctieX;
	
	private CssColor drawingColor = CssColor.make(80, 80, 80);
	private Rectangle defaultBox;
	private Rectangle box;
	private Rectangle writeBox;
	private boolean correct = false;
	private boolean isfalse = false;
	private boolean isHalf = false;
	
	private boolean isInputSC = false;
	
	private FormuleViewer formuleViewer;
	
	//private double schrijfLeesFactor = 2;
	//private boolean checkable;
	private static Logger logger = Logger.getLogger("KStrokeContainer");
	
	
	public KStrokeContainer (KladjeGWTVeld parent) {
		this.parent = parent;
		
		strokeContainer = new StrokeContainer();
	}
	
	public KStrokeContainer (KladjeGWTVeld parent, Rectangle defaultBox) {
		this.parent = parent;
		this.defaultBox = defaultBox;
		this.box = defaultBox;
		isInputSC = true;
		strokeContainer = new StrokeContainer();
	}
	
	public boolean addStroke(Stroke stroke) {
		box = null;
		writeBox = null;
		boolean b = strokeContainer.addStroke(stroke);
		logger.info(strokeContainer.getFormulaString());
		//if(!"-".equals(stroke.getOneStrokeTeken()))
		formuleViewer = new FormuleViewer(strokeContainer.getFormulaString());
		formuleViewer.setColor(CssColor.make(38, 115, 182));
		//formuleViewer.setFont(FormuleFont.createFromFontSize(16));
		
		corrigeerSCPositie();
		if(getStrokeCount()==0)
			box = defaultBox;
		
		return b;
	}
	
	public void corrigeerSCPositie() {
		if(active && getBox()!=null) {
			int correctieX = Math.max(0, getBox().x+getBox().width+80+47 - parent.breedte-20);
			int correctieY = Math.min(0,getBox().y-70);
			activeTranslationX += correctieX;
			activeTranslationY += correctieY; 
			
			translate((int)-correctieX,(int)-correctieY);
		}
	}
	
	public void wis() {
		strokeContainer.wis();
	}
	
	public int getStrokeCount() {
		return strokeContainer.getStrokes().size();
	}
	
	public Rectangle getCloseButtonArea() {
		if(writeBox==null)
			writeBox = new Rectangle(20,20,parent.breedte-40,parent.hoogte-40);
		int x = getWriteBox().x + getWriteBox().width - 33; 
		int y = getWriteBox().y + 6; 
		return new Rectangle(x,y,30,30);
	}
	
	public Rectangle getCheckButtonArea() {
		if(writeBox==null)
			writeBox = new Rectangle(20,20,parent.breedte-40,parent.hoogte-40);
		int x = getWriteBox().x + getWriteBox().width - 33; 
		int y = getWriteBox().y + getWriteBox().height - 36; 
		return new Rectangle(x,y,30,30);
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
	
	private void drawCheckButton(Context2d g, Rectangle r) {
		//int m = 10;
		
		//g.setFillStyle(CssColor.make(255, 255, 255));
		
		//g.fillRect(r.x, r.y, r.width, r.height);
		g.setStrokeStyle(CssColor.make(38, 115, 182));
		g.setLineWidth(4.0d);
		g.beginPath();
		g.moveTo(r.x+r.width/4 , r.y+r.height/2);
		g.lineTo(r.x+r.width/2, r.y+r.height);
		g.lineTo(r.x+r.width, r.y);
		g.moveTo(r.x , r.y);
		g.closePath();
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
				
					int	lineDistance = (int)(10*parent.schrijfLeesFactor);
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
			if(active && !popupMode) {
				g.setFillStyle(CssColor.make(255, 255, 255));
				g.fillRect(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10);
				g.setFillStyle(CssColor.make(255, 243, 180));
				g.fillRect(getWriteBox().x+getWriteBox().width-42, getWriteBox().y-5, 47, getWriteBox().height+10);
				drawShadow(g,new Rectangle(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10));
				drawGrid(g,new Rectangle(getWriteBox().x-5, getWriteBox().y-5, getWriteBox().width+10, getWriteBox().height+10));
				
				//g.setFillStyle(CssColor.make(239, 241, 243));
				g.fillRect(getWriteBox().x + getWriteBox().width-40, getWriteBox().y, 40, 40);
				
//				g.setStrokeStyle(CssColor.make(255, 0, 0));
//				
//				g.beginPath();
//				g.rect(getBox().x,(int)strokeContainer.averageBaseLine-strokeContainer.averageHeight, getBox().width, strokeContainer.averageHeight);
//				g.closePath();
//				g.stroke();
//				
//				g.setStrokeStyle(CssColor.make(80, 80, 80));

				drawcloseButton(g, getCloseButtonArea());
				

//				int d = (int)strokeContainer.averageHeight;
//				g.setFillStyle(CssColor.make(0,0,0));
//				g.fillText(""+d, 300, 20);
				
				if(formuleViewer!=null) {
					int x = Math.max(getWriteBox().x+50, getBox().x) ;// + getBox().width/2-parent.formuleViewer.getWidth()/2;
					int y = getWriteBox().y+5;//-20-formuleViewer.getHeight();
					g.translate(x, y);
					formuleViewer.getMainRegel().paintAll(g);
					g.translate(-x, -y);
				}
				
				if(parent.eigenaar.comRoot.hasListeners("action.check"))
					drawCheckButton(g, getCheckButtonArea());
				
//				g.setFillStyle(CssColor.make(255, 255, 255));
//				
//				g.fillRect(getWriteBox().x + getWriteBox().width-77, getWriteBox().y+3, 34, 34);
//				g.setStrokeStyle(CssColor.make(0, 200, 0));
//				g.setLineWidth(5.0d);
//				g.beginPath();
//				g.moveTo(getWriteBox().x + getWriteBox().width-70, getWriteBox().y+10);
//				g.lineTo(getWriteBox().x + getWriteBox().width-60, getWriteBox().y+30);
//				g.lineTo(getWriteBox().x + getWriteBox().width-40, getWriteBox().y+0);
//				g.moveTo(getWriteBox().x + getWriteBox().width-70, getWriteBox().y+10);
//				g.closePath();
//				g.stroke();
				
				g.setStrokeStyle(drawingColor);
				g.setLineWidth(3.0d);
				
				if(correct||isfalse||isHalf) {
					//g.setFillStyle(CssColor.make(240, 255, 240));
					if(correct)
						g.drawImage(parent.goedvinkImageElement, getWriteBox().x + 20-14, getWriteBox().y + 20-14);
						//g.setFillStyle(CssColor.make(0, 200, 0));
					if(isfalse)
						g.drawImage(parent.foutkruisImageElement, getWriteBox().x + 20-14, getWriteBox().y + 20-14);
						//g.setFillStyle(CssColor.make(200, 0, 0));
					if(isHalf)
						g.drawImage(parent.halfvinkImageElement, getWriteBox().x + 20-14, getWriteBox().y + 20-14);
						//g.setFillStyle(CssColor.make(240, 240, 0));
//					g.beginPath();
//					g.arc(getWriteBox().x + 20, getWriteBox().y + 20 , 8, 0, 8* Math.PI);
//					g.closePath();
//					g.stroke();
//					g.fill();
				}
				
			}
			else {
				//g.setFillStyle(CssColor.make(243, 241, 239));
				g.setFillStyle(CssColor.make(255, 255, 255));
				g.setLineWidth(1.5d);
				if(correct||isfalse||isHalf) {
					//g.setFillStyle(CssColor.make(240, 255, 240));
					//g.fillRect(getBox().x-30, getBox().y-5, getBox().width+35, getBox().height+10);
					if(correct)
						g.drawImage(parent.goedvinkImageElement, getBox().x-20-14, getBox().y+getBox().height/2-7);
						//g.setFillStyle(CssColor.make(0, 200, 0));
					if(isfalse)
						g.drawImage(parent.foutkruisImageElement, getBox().x-20-14, getBox().y+getBox().height/2-7);
						//g.setFillStyle(CssColor.make(200, 0, 0));
					if(isHalf)
						g.drawImage(parent.halfvinkImageElement, getBox().x-20-14, getBox().y+getBox().height/2-7);
						//g.setFillStyle(CssColor.make(240, 240, 0));
//					g.beginPath();
//					g.arc(getBox().x-20, getBox().y+getBox().height/2 , 5, 0, 5* Math.PI);
//					g.closePath();
//					g.stroke();
//					g.fill();
				}
				//else
					//g.fillRect(getBox().x-5, getBox().y-5, getBox().width+10, getBox().height+10);
			}	
		}
		
		if(active && popupMode) {
			
				Rectangle wbox = new Rectangle(20,20,parent.breedte-40,parent.hoogte-40);
				g.setFillStyle(CssColor.make(255, 255, 255));
				g.fillRect(wbox.x, wbox.y, wbox.width, wbox.height);
				g.setFillStyle(CssColor.make(255, 243, 180));
				g.fillRect(wbox.x+wbox.width-42, wbox.y-5, 47, wbox.height+10);
				drawShadow(g,new Rectangle((int)wbox.x-5, (int)wbox.y-5, (int)wbox.width+10, (int)wbox.height+10));
				drawGrid(g,new Rectangle((int)wbox.x-5, (int)wbox.y-5, (int)wbox.width+10, (int)wbox.height+10));
				
				g.setStrokeStyle(CssColor.make(80, 80, 80));
				
				//g.setFillStyle(CssColor.make(239, 241, 243));
				//g.fillRect(wbox.x + wbox.width-40, wbox.y, 40, 40);

				drawcloseButton(g, getCloseButtonArea());
				if(formuleViewer!=null) {
					int x = Math.max(wbox.x+50, getBox()!=null ? getBox().x : 0) ;// + getBox().width/2-parent.formuleViewer.getWidth()/2;
					int y = wbox.y+5;//-20-formuleViewer.getHeight();
					g.translate(x, y);
					formuleViewer.getMainRegel().paintAll(g);
					g.translate(-x, -y);
				}
				
				if(parent.eigenaar.comRoot.hasListeners("action.check"))
					drawCheckButton(g, getCheckButtonArea());
				
				if(correct||isfalse||isHalf) {
					//g.setFillStyle(CssColor.make(240, 255, 240));
					if(correct)
						g.setFillStyle(CssColor.make(0, 200, 0));
					if(isfalse)
						g.setFillStyle(CssColor.make(200, 0, 0));
					if(isHalf)
						g.setFillStyle(CssColor.make(240, 240, 0));
					g.beginPath();
					g.arc(wbox.x + 20, wbox.y + 20 , 8, 0, 8* Math.PI);
					g.closePath();
					g.stroke();
					g.fill();
				}
			
		}
		
		
		
		//g.setStrokeStyle(drawingColor);
		if(!strokeContainer.isParseable())
			g.setStrokeStyle(CssColor.make(38, 115, 182));
		else
			g.setStrokeStyle(CssColor.make(80, 80, 80));
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
	
	public void setDefaultRectangle(Rectangle r) {
		defaultBox = r;
		if(box==null)
			box = defaultBox;
	}
	
	public void setCorrect(boolean correct) {
//		if(!correct && this.correct)
//			activeTranslation -=25;
//		if(correct && !this.correct)
//			activeTranslation -=25;
		this.correct = correct;
		if(correct) {
			isfalse = false;
			isHalf = false;
		}
	}
	
	public void setFalse(boolean isfalse) {
//		if(!isfalse && this.isfalse)
//			activeTranslation -=25;
//		if(isfalse && !this.isfalse)
//			activeTranslation -=25;
		this.isfalse = isfalse;
		if(isfalse) {
			correct = false;
			isHalf = false;
		}
	}
	public void setHalf(boolean isHalf) {
//		if(!isfalse && this.isfalse)
//			activeTranslation -=25;
//		if(isfalse && !this.isfalse)
//			activeTranslation -=25;
		this.isHalf = isHalf;
		if(isHalf) {
			correct = false;
			isfalse = false;
		}
	}
	
//	public void setActive (boolean b) {
//		active = b;
//		if(active && getBox()!=null) {
//			activeTranslationX = getBox().x - 40;
//			activeTranslationY = 0; 
//			if(getBox().y<70)
//				activeTranslationY = getBox().y-70;
//			if(getBox().y+getBox().height>parent.hoogte-70) 
//				activeTranslationY = getBox().y+getBox().height - (parent.hoogte-70);
//			translate((int)-activeTranslationX,(int)-activeTranslationY);
//		}
//		else if(getBox()!=null)
//			translate((int)activeTranslationX, (int)activeTranslationY);
//	}
	
	public void setActive (boolean b) {
		active = b;
		if(active && getBox()!=null) {
			activeTranslationX = Math.max(0, getBox().x+getBox().width+100+47 - parent.breedte-20);
			activeTranslationY = 0; 
			if(getBox().y<70)
				activeTranslationY = getBox().y-70;
			if(getBox().y+getBox().height>parent.hoogte-70) 
				activeTranslationY = getBox().y+getBox().height - (parent.hoogte-70);
			translate((int)-activeTranslationX,(int)-activeTranslationY);
		}
		else if(getBox()!=null)
			translate((int)activeTranslationX, (int)activeTranslationY);
	}
	
	public void setpopupMode (boolean b) {
		popupMode = b;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getFormulaString() {
		if(strokeContainer.isParseable())
			return strokeContainer.getFormulaString();
		return "";
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
			if(width<0 || height<0)
				box=null;
				
		}
		if(box==null)
			box = defaultBox;
		return box;
	}
	
	public Rectangle getDragBox() {
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
			int xSC = (int)strokeContainer.getBoundingBox().x;
			int ySC = (int)strokeContainer.getBoundingBox().y;
			int widthSC = (int)strokeContainer.getBoundingBox().width;
			int heightSC = (int)strokeContainer.getBoundingBox().height;
			
			int x = Math.max(20,xSC - margin);
			int y = (int)Math.max(20, ySC - margin-10);
			
			int width = Math.min(parent.breedte-40, widthSC+3*margin+47); //47 is breedte gele strook met knoppen)
			int height = Math.min(parent.hoogte-40, heightSC + 2*margin);
			
			x = (int)Math.min(x, parent.breedte - width -20);
			y = (int)Math.min(y, parent.hoogte - height -20);
			
			
			writeBox = new Rectangle(x, y, width, height);
		}
//		else if(writeBox==null && strokeContainer != null) { //strokeContainer.getBoundingBox()==null
//			int width = parent.breedte-40;
//			int height = Math.min(parent.hoogte-40, defaultBox.height + 2*margin);
//			
//			int x = 20;
//			int y = (int)Math.max(20,defaultBox.y - margin-10);
//			y= (int)Math.min(y, parent.hoogte - height -20);
//			
//			
//			writeBox = new Rectangle(x, y, width, height);
//		}
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
		if(strokeContainer.getStrokes().size()==0 || strokeContainer.getDiagonal()<15)
			return true;
		return false;
	}
	
	public boolean isNotRelevantWhenReady() {
		if(strokeContainer.getStrokes().size()==1) {
			double length = strokeContainer.getStrokes().get(0).getLength();
			if(length<35)
				return true;

		}
		if(strokeContainer.getStrokes().size()==2) {
			double length1 = strokeContainer.getStrokes().get(0).getLength() ;
			double length2 = strokeContainer.getStrokes().get(1).getLength();
			if(length1<35 && length2<35)
				return true;

		}
		if(strokeContainer.getStrokes().size()==3) {
			double length1 = strokeContainer.getStrokes().get(0).getLength() ;
			double length2 = strokeContainer.getStrokes().get(1).getLength();
			double length3 = strokeContainer.getStrokes().get(2).getLength();
			if(length1<35 && length2<35 && length3<35)
				return true;

		}
		return false;
		
	}
	
	public boolean contains(int x, int y, int margin) {
		if(strokeContainer != null && strokeContainer.getBoundingBox()!=null) {
			int xb = (int)strokeContainer.getBoundingBox().x;
			int yb = (int)strokeContainer.getBoundingBox().y;
			int width = (int)strokeContainer.getBoundingBox().width;
			int height = (int)strokeContainer.getBoundingBox().height;
			int leftMargin = 0;
			if(correct||isHalf||isfalse)
				leftMargin = 40;
			Rectangle box = new Rectangle(xb-margin-leftMargin, yb-margin, width+2*margin+leftMargin, height+2*margin);
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
		map.put("isHalf", new Boolean(isHalf));
		return map;
	}
	
	public void setState (Map<String,Object> map) {
		strokeContainer.setState(map);
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		if(launchState.containsKey("correct"))
			correct = launchState.getBoolean("correct");
		if(launchState.containsKey("isfalse"))
			isfalse = launchState.getBoolean("isfalse");
		if(launchState.containsKey("isHalf"))
			isHalf = launchState.getBoolean("isHalf");
		formuleViewer = new FormuleViewer(strokeContainer.getFormulaString());
		formuleViewer.setColor(CssColor.make(38, 115, 182));
	}
}
