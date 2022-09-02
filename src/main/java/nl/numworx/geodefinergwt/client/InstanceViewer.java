package nl.numworx.geodefinergwt.client;

import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegClosePath;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.CELL;
import nl.numworx.geodefiner.common.Integral;
import nl.numworx.geodefiner.common.ShortSegment;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefinergwt.client.SnapperImpl.PH;
import nl.numworx.geodefinergwt.client.ui.AxesModel;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;
import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.euclides.event.NameMapper;
import fi.euclides.event.TrackerContext;
import fi.euclides.gwt.svg.SVGRectShape;
import fi.euclides.gwt.svg.SVGWidget;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Triangle;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.FlipFlop;
import fi.euclides.util.Adapter;
import fi.euclides.util.DefaultAdapter;
import gwt.awt.Shape;
import gwt.awt.geom.Area;
import gwt.awt.geom.PathIterator;

public class InstanceViewer extends SVGWidget implements PH {

	private static final float DEFAULT_POINTSIZE = 5;
	private AnimationHandle animator;

	@Override
	public void processMouseUp(int x0, int y0, int id) {
		snapper.pmUp(x0, y0, id, this);
	}
	@Override
	public void pmUp(Numbers x, Numbers y, int id) {
		super.processMouseUp(x.intValue(), y.intValue(), id);
	}
	@Override
	public void processMouseDrag(int x0, int y0, int id) {
		snapper.pmDrag(x0, y0, id, this);
	}
	
	@Override
	public void pmDrag(Numbers x, Numbers y, int id) {
		super.processMouseDrag(x.intValue(), y.intValue(), id);
	}

	@Override
	public void processMouseDown(int x, int y,  int id) {
		snapper.setMoved(false);
		super.processMouseDown(x, y, id);
	}

	private NameMapper mapper = super.getMapper();
	@Override public NameMapper getMapper() { return mapper; }
	@Override public void setMapper(NameMapper n) { mapper = n; }

	private SnapperImpl snapper = new SnapperImpl();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T adapt(Class<T> cls) {
		if(cls == Snapper.class) return (T) snapper;		
		return super.adapt(cls);
	}

	public InstanceViewer() {
		hitTester = new HitTesterGWT();
	}

	public InstanceViewer(int width, int height) {
		this();
		init(width, height);
	}

	private StrokeStyle stroke;
	
	public void selectColor(Destroyable object) {
		Adapter a = object.getAdapter();
		stroke = a.adapt(StrokeStyle.class);
		if(tracking || trail)
			return;

		FillStyle f = a.adapt(FillStyle.class);
		if(f == null) fill = "none";
		else fill = f.getColor();
		
		ColorStyle c = a.adapt(ColorStyle.class);
		if (c != null) {
			// if selected?
			setCssColor(CssColor.make(c.getColor()));
			return;
		}
		super.selectColor(object);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		OMSVGLineElement line = doc.createSVGLineElement((float)x1, (float)y1, (float)x2, (float)y2);
		OMSVGStyle style = line.getStyle();
		style.setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
		if(stroke != null) stroke.toStyle(style);
		getBody().appendChild(line);
	}

	
	public void visitFormule(Label label) {
		Align align = label.adapt(Align.class);
		if(align == null) align = Align.BASE;
		else if(align == Align.NONE) return;
		FormuleViewer viewer = new FormuleViewer(label.getString());
		FontStyle fs = label.adapt(FontStyle.class);
		if(fs != null) viewer.setFont(fs.getFont());
		String url = viewer.getMainRegel().getCanvas().toDataUrl();
		float w = viewer.getWidth();
		float h = viewer.getHeight();
		float as = viewer.getAsHoogte();
		float x = (float) label.getXd();
		float y = (float) label.getYd();
		switch(align) {
		default: y -= as; break;
		case LEFT: x -= w;
		case RIGHT: y -= h/2.0f; break;
		case TOP: y -= h;
		case BOTTOM: x -= w/2.0f; break;
		}
		OMSVGImageElement image = doc.createSVGImageElement(x, y, w, h, url);
		getBody().appendChild(image);
		OMSVGRect bbox = image.getBBox();
		DefaultAdapter.getDefault(label).put(Shape.class, new SVGRectShape(bbox));

	}
	
	private void visitFlipFlop(Label label) {
		boolean value = label.getState() != Label.FALSE;
		String off = "none";
		String on = "gray";
		float x = (float) label.getXd();
		float y = (float) label.getYd();
		OMSVGGElement g = doc.createSVGGElement();
		OMSVGRectElement rect = doc.createSVGRectElement(x, y, 10, 10, 1, 1);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, value?on:off);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "black");
		String string = getMapper().toString(label);
		g.appendChild(rect);
		if(Align.NONE != label.adapt(Align.class))
		{
			OMSVGTextElement text = doc.createSVGTextElement(x+12, y+10, OMSVGLength.SVG_LENGTHTYPE_NUMBER,string);
			g.appendChild(text);
		}
		getBody().appendChild(g);
		OMSVGRect bbox = g.getBBox();
		DefaultAdapter.getDefault(label).put(Shape.class, new SVGRectShape(bbox));
	}
	
	private void visitAnimateur(Label l) {
		boolean value = l.getState() != Label.FALSE;
		String off = "none";
		String on = "gray";
		float x = (float) l.getXd();
		float y = (float) l.getYd();
		OMSVGGElement g = doc.createSVGGElement();
		OMSVGEllipseElement rect = doc.createSVGEllipseElement(x, y, 10, 10);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, value?on:off);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "black");
		g.appendChild(rect);
		getBody().appendChild(g);
		OMSVGRect bbox = g.getBBox();
		DefaultAdapter.getDefault(l).put(Shape.class, new SVGRectShape(bbox));
	}
	
	
	@Override
	public void visitLabel(Label label) {
		if(label.getRegistered() instanceof FlipFlop) {
			visitFlipFlop(label);
			return;
		}
		if(label.getString().contains("$")||Boolean.TRUE.equals(label.adapt(Boolean.class)))
		{
			visitFormule(label);
			return;
		}
		selectColor(label);
		String string = label.getString();
		double x = label.getXd();
		double y = label.getYd();
		Align align = label.adapt(Align.class);
		String h = null;
		String v = null;
		if(align != null) {
			switch(align) {
			case LEFT:   h = TEXT_END;   v = TEXT_CENTRAL; break; 
			case RIGHT:  h = TEXT_START; v = TEXT_CENTRAL; break;
			case BOTTOM: h = TEXT_MIDDLE;v = TEXT_TOP;     break;
			case TOP:    h = TEXT_MIDDLE;v = TEXT_BOTTOM;  break;
			default: 
			}
		} else align = Align.BASE;
		short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
		OMSVGTextElement text = doc.createSVGTextElement((float)x, (float)y, unitType, string);
		OMSVGStyle style = text.getStyle();
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
		if(h != null) style.setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, h);
		if(v != null) style.setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, v);
		FontStyle fs = label.adapt(FontStyle.class);
		if(fs != null) fs.toStyle(style);
		getBody().appendChild(text);
		OMSVGRect bbox = text.getBBox();
		switch(align) {
		case LEFT:	x += x - bbox.getMaxX(); break; //als maxx > x dan x moet minder worden
		case BASE:
		case NONE:
		case RIGHT: x += x - bbox.getX(); break;
		case BOTTOM: 
		case TOP:	x += x - bbox.getCenterX(); break;
		}
		switch(align) {
		case LEFT:	
		case RIGHT: y += y - bbox.getCenterY(); 
			break;
		case BASE:
		case NONE: break;
		case BOTTOM: y += y - bbox.getY(); break;
		case TOP:	y += y - bbox.getMaxY(); break;
		}
// This is how to position after bbox FIXME DOES NOT WORK?
		text.getX().getBaseVal().getItem(0).setValue((float) x);
		text.getY().getBaseVal().getItem(0).setValue((float) y);
		
		bbox = text.getBBox();
		DefaultAdapter.getDefault(label).put(Shape.class, new SVGRectShape(bbox));
	}

	static final String grayish = "rgba(32,32,32, 0.125)";
	static final String reddish = "rgba(128,0,0, 0.125)";

	/* (non-Javadoc)
	 * @see fi.euclides.model.AbstractViewer#visitTriangle(fi.euclides.model.Triangle)
	 */
	@Override
	public void visitTriangle(Triangle t) {
		selectColor(t);

		Punt[] depend = (Punt[]) t.getDepend();
		int length = depend.length;
		OMSVGPathElement path = doc.createSVGPathElement();
		OMSVGPathSegList points = path.getPathSegList();
		float x = (float) depend[0].getXd();
		float y = (float) depend[0].getYd();
		points.appendItem(path.createSVGPathSegMovetoAbs(x, y));		
		for (int i = 1; i < length; i++) {
			Punt p = depend[i];
			x = (float) p.getXd();
			y = (float) p.getYd();
			points.appendItem(path.createSVGPathSegLinetoAbs(x, y));
		}
		points.appendItem(path.createSVGPathSegClosePath());
		OMSVGStyle style = path.getStyle();
		style.setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, fill);
		style.setSVGProperty(SVGConstants.CSS_FILL_RULE_PROPERTY, SVGConstants.CSS_EVENODD_VALUE);
		getBody().appendChild(path);
	}

	/* (non-Javadoc)
	 * @see fi.euclides.model.AbstractViewer#visitPunt(fi.euclides.model.Punt)
	 */
	@Override
	public void visitPunt(Punt punt) {
		Float ps = punt.adapt(Float.class);
		if(ps != null) 
			pointSize = ps.floatValue();
		else
			pointSize = DEFAULT_POINTSIZE;
		super.visitPunt(punt);
	}

	private Segment drawTips(Segment s) {
		Tips tip = s.adapt(Tips.class);
		if(tip == null) return s;
		selectColor(s);
		float dx = (float) s.getDX();
		float dy = (float) s.getDY();
		float len = (float) Math.hypot(dx, dy);
		Float width = s.adapt(Float.class);
		float tiplen = 5;
		if(width != null) tiplen *= width.doubleValue();
		if(len < tiplen*3) tiplen = len/3;
		dx *= tiplen/len; 
		dy *= tiplen/len; 
		switch(tip) {
		case ATEND: tip(s.getP2(), -dx, -dy); break;
		case ATSTARTEND: tip(s.getP2(),-(dx), -(dy));
		case ATSTART: tip(s.getP1(), dx, dy);break;
		case NOTIP: return s;
		}
		return new ShortSegment(s, dx, dy, tip);
	}

	private void tip(Punt p1, float dx, float dy) {
//		Path2D.Double path = new Path2D.Double();
		float x = (float)p1.getXd();
		float y = (float)p1.getYd();
//		path.moveTo(x, y);
//		path.lineTo(x + dx + dy/2, y + dy -dx/2);
//		path.lineTo(x + dx - dy/2, y + dy +dx/2);
//		path.closePath();
//		g.fill(path);
		OMSVGPathElement path = doc.createSVGPathElement();
		OMSVGPathSegList points = path.getPathSegList();
		points.appendItem(path.createSVGPathSegMovetoAbs(x, y));		
		points.appendItem(path.createSVGPathSegLinetoAbs(x + dx + dy/2, y + dy -dx/2));
		points.appendItem(path.createSVGPathSegLinetoAbs(x + dx - dy/2, y + dy +dx/2));
		points.appendItem(path.createSVGPathSegClosePath());
		OMSVGStyle style = path.getStyle();
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
		getBody().appendChild(path);
	}
	
	@Override
	public void visitSegment(Segment s) {
		s = drawTips(s);
		super.visitSegment(s);
		
	}
	/** Lazy
	 * @see fi.euclides.gwt.svg.SVGWidget#paint()
	 */
	@Override
	public void paint() {
		if(animator == null)
		animator = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			@Override
			public void execute(double timestamp) {
				animator = null;
				doPaint();
			}
		}, getSvgElement().getElement());
	}

	private void doPaint() {
		super.paint();
	}

	@Override
	public void drawAxes() {
		Destroyable grid = getModel().getLijnen().elementAt(2);
		if(grid.isVisible()) grid.visit(this);
//draw grid
		Lijn x = (Lijn) getModel().getLijnen().firstElement();
		if(grid.isVisible() && !x.isVisible() && x.isDefined()) {
			// draw x in grid mode
			ll.setLijn(x);
			drawLine(ll.getX1(), ll.getY1() , ll.getX2(), ll.getY2());		
		}
		Lijn y = (Lijn) getModel().getLijnen().elementAt(1);
		if(grid.isVisible() && !y.isVisible() && y.isDefined()) {
			// draw x in grid mode
			ll.setLijn(y);
			drawLine(ll.getX1(), ll.getY1() , ll.getX2(), ll.getY2());		
		}
		color = "black";
		CELL item = x.adapt(CELL.class);
		boolean bx = false, by = false;
		if (item != null) {
			AxesModel configX = (AxesModel) item.config;
			bx = configX != null && configX.numbers && x.isVisible();
			if(bx) { drawXnumbers(); }
		}
		item = y.adapt(CELL.class);
		if (item != null) {
			AxesModel configY = (AxesModel) item.config;
			by = configY != null && configY.numbers && y.isVisible();
			if(by) { drawYnumbers(); }
		}
		if (bx || by) drawO();
	}

	String background = "white";
	
	private void drawXnumbers() {
		double left = clipLeft().doubleValue();
		double right = clipRight().doubleValue();
		double x = getModel().getO().getXd();
		double y = getModel().getO().getYd();
		double dx = getModel().getU().getXd() - x;
		if(dx <= 1) return;
		int i = 0, s = 1;
		while(dx < 20) { dx += dx; s+=s; if(dx >= 20) break; dx = 2.5*dx; s += s+s/2; if(dx >= 20) break; dx += dx; s += s; }
		left -= dx;i=s;
		for(double xr = x+dx ; xr < right; xr += dx, i+=s) {
			String value = String.valueOf(i);
			drawString(value, xr, y, TEXT_MIDDLE, TEXT_TOP, background);
		}
		i = -s;
		for(double xr = x-dx ; xr > left; xr -= dx, i-=s) {
			String value = String.valueOf(i);
			drawString(value, xr, y, TEXT_MIDDLE, TEXT_TOP, background);
		}
		
	}


	private void drawYnumbers() {
		double bottom = clipBottom().doubleValue();
		double top = clipTop().doubleValue();
		double x = getModel().getO().getXd()-2;
		double y = getModel().getO().getYd();
		double dy = getModel().getU().getXd() - x;
		if (dy <= 1) return;
		int i = 0, s = 1;
		while(dy < 20) { dy += dy; s+=s; if(dy >= 20) break; dy = 2.5*dy; s += s+s/2; if(dy >= 20) break; dy += dy; s += s; }
		i=s;
		for(double yr = y-dy ; yr > top; yr -= dy, i+=s) {
			String value = String.valueOf(i);
			drawString(value, x, yr, TEXT_END, TEXT_CENTRAL, background);
		}
		i = -s;
		bottom += dy;
		for(double yr = y+dy; yr < bottom; yr += dy, i-=s) {
			String value = String.valueOf(i);
			drawString(value, x, yr, TEXT_END, TEXT_CENTRAL, background);
		}
	}
	private void drawO() {
		double x, y;
		x = getModel().getO().getXd();
		y = getModel().getO().getYd();
		color = "black";
		drawString("0", x, y, TEXT_END, TEXT_TOP, background);
	}

	private void visitIntegral(Integral l) {
		selectColor(l);
		Area shape = new Area();
		l.visitSegments(new IntegralVisitor(l, shape, this));
//g.fill(shape);
		PathIterator iter = shape.getPathIterator(null);
		OMSVGPathElement path = doc.createSVGPathElement();
		OMSVGPathSegList points = path.getPathSegList();
		while(!iter.isDone()) {
			float[] p = new float[6];
			switch( iter.currentSegment(p)) {
			case PathIterator.SEG_MOVETO: 
				OMSVGPathSegMovetoAbs move = path.createSVGPathSegMovetoAbs(p[0], p[1]);
				points.appendItem(move);
				break;
			case PathIterator.SEG_LINETO:
				OMSVGPathSegLinetoAbs line = path.createSVGPathSegLinetoAbs(p[0], p[1]);
				points.appendItem(line);
				break;
			case PathIterator.SEG_CLOSE:
				OMSVGPathSegClosePath close = path.createSVGPathSegClosePath();
				points.appendItem(close);
				break;
			}
			iter.next();
		}
		OMSVGStyle style = path.getStyle();
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
		style.setSVGProperty(SVGConstants.CSS_FILL_RULE_PROPERTY, SVGConstants.CSS_EVENODD_VALUE);
		getBody().appendChild(path);

		DefaultAdapter.getDefault(l).put(Shape.class, shape);
	}
	@Override
	public void visitLocus(Locus l) {
		if(l instanceof Integral) {
			visitIntegral( (Integral) l);
		} else
		super.visitLocus(l);
	}

	@Override
    public TrackerContext getCtx(int id) {
      return this;
    }

}
