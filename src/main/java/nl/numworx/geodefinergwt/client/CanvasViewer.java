package nl.numworx.geodefinergwt.client;

import java.awt.geom.GeneralPath;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.euclides.event.NameMapper;
import fi.euclides.gwt.RectShape;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Locus;
import fi.euclides.model.MP;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.SegmentVisitor;
import fi.euclides.model.Triangle;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.FlipFlop;
import fi.euclides.util.Adapter;
import fi.euclides.util.DefaultAdapter;
import gwt.awt.Shape;
import gwt.awt.geom.Area;
import gwt.awt.geom.Path2D;
import gwt.awt.geom.PathIterator;
import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.Integral;
import nl.numworx.geodefiner.common.Interval;
import nl.numworx.geodefiner.common.ShortSegment;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;
import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class CanvasViewer extends SpeelVeld implements SnapperImpl.PH {
	private static final FontStyle FONT_STYLE = new FontStyle();

	private static final float DEFAULT_POINTSIZE = 5;
	private static final StrokeStyle DEFAULT_STROKE = new StrokeStyle(1, null);
	private AnimationHandle animator;

	private NameMapper mapper = super.getMapper();
	@Override public NameMapper getMapper() { return mapper; }
	@Override public void setMapper(NameMapper n) { mapper = n; }

	public CanvasViewer(int width, int height) {
		super(width, height);
		hitTester = new HitTesterGWT();
	}

	public CanvasViewer() {
		this(200, 200);
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
		}, canvas.getElement());
	}

	private void doPaint() {
		super.paint();
	}

	SnapperImpl snapper = new SnapperImpl();
	private StrokeStyle stroke;
	@Override
	public void processMouseUp(int x0, int y0) {
		snapper.pmUp(x0, y0, this);
	}
	public void pmUp(int x, int y) {
		super.processMouseUp(x, y);
	}
	@Override
	public void processMouseDrag(int x0, int y0) {
		snapper.pmDrag(x0, y0, this);
	}
	public void pmDrag(int x, int y) {
		super.processMouseDrag(x, y);
	}

	@Override
	public void processMouseDown(int x, int y) {
		snapper.setMoved(false);
		super.processMouseDown(x, y);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T adapt(Class<T> cls) {
		if(cls == Snapper.class) return (T) snapper;
		if(cls == AbstractViewer.class) return (T) this;
		return super.adapt(cls);
	}

	@Override
	public void drawAxes() {
		new AxesDrawer(this).drawAxes();
	}

	
	@Override
	public void visitTriangle(Triangle t) {
		selectColor(t);
		Punt[] depend = (Punt[]) t.getDepend();
		Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, depend.length);
		int length = depend.length;
		double x = depend[0].getXd();
		double y = depend[0].getYd();
		context.beginPath();
		context.moveTo(x, y);
		path.moveTo(x, y);
		for (int i = 1; i < length; i++) {
			Punt p = depend[i];
			x = p.getXd();
			y = p.getYd();
			context.lineTo(x, y);
			path.lineTo(x, y);
		}
		context.closePath();
		path.closePath();
		if(!"none".equals(fill))
		{	context.setFillStyle(fill);
			context.fill();
		}
		if(stroke != null) stroke.toStyle(context);
		context.stroke();
		DefaultAdapter.getDefault(t).put(Shape.class, path);
	}

	public void selectColor(Destroyable object) {
		if(tracking || trail)
			return;
		Adapter a = object.getAdapter();
		//java.util.logging.Logger.getLogger("CanvasViewer").info(object  + ".adapter=" + a);
		stroke = a.adapt(StrokeStyle.class);
		DEFAULT_STROKE.toStyle(context);
		FillStyle f = a.adapt(FillStyle.class);
		if(f == null) fill = "none";
		else fill = f.getColor();
		
		ColorStyle c = a.adapt(ColorStyle.class);
		//java.util.logging.Logger.getLogger("CanvasViewer").info("color = " + c);
		if (c != null) {
			// if selected?
			setCssColor(CssColor.make(c.getColor()));
			return;
		}
		super.selectColor(object);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		if(stroke != null) stroke.toStyle(context);
		super.drawLine(x1, y1, x2, y2);
	}

	@Override
	protected void drawCircle(double i, double j, double k) {
		if(stroke != null) stroke.toStyle(context);
		super.drawCircle(i, j, k);
	}

	@Override
	protected void drawArc(double x, double y, double k, double start,
			double length) {
		if(stroke != null) stroke.toStyle(context);
		super.drawArc(x, y, k, start, length);
	}

	private Segment drawTips(Segment s) {
		Tips tip = s.adapt(Tips.class);
		if(tip == null) return s;
		selectColor(s);
		double dx = s.getDX();
		double dy = s.getDY();
		double len = Math.hypot(dx, dy);
		Float width = s.adapt(Float.class);
		double tiplen = 5;
		if(width != null) tiplen *= width.doubleValue();
		if(len < tiplen*3) tiplen = len/3;
		dx *= tiplen/len; 
		dy *= tiplen/len; 
		switch(tip) {
		case ATEND: tip(s.getP2(), -dx, -dy); break;
		case ATSTARTEND: tip(s.getP2(),-(dx), -(dy));
		case ATSTART: tip(s.getP1(), dx, dy); break;
		case NOTIP: return s;
		}
		return new ShortSegment(s, dx, dy, tip);
	}

	private void tip(Punt p1, double dx, double dy) {
		double x = (float)p1.getXd();
		double y = (float)p1.getYd();
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x + dx + dy/2, y + dy -dx/2);
		context.lineTo(x + dx - dy/2, y + dy +dx/2);
		context.closePath();
		context.fill();
	}

	@Override
	public void visitSegment(Segment s) {
		s = drawTips(s);
		super.visitSegment(s);
		
	}

	@Override
	public void visitPunt(Punt punt) {
		Float ps = punt.adapt(Float.class);
		if(ps != null) 
			pointSize = ps.floatValue();
		else
			pointSize = DEFAULT_POINTSIZE;
		super.visitPunt(punt);
	}
	class PathVisitor implements SegmentVisitor {
		
		double x = Double.NEGATIVE_INFINITY;
		double y = Double.NEGATIVE_INFINITY;
		
		@Override
		public void visitSegment(Segment s) {
			double x1, y1;
			x1 = s.getX1();
			y1 = s.getY1();
			if(x1 != x || y1 != y)
				context.moveTo(x1, y1);
			x = s.getX2();
			y = s.getY2();
			context.lineTo(x, y);
		}

		@Override
		public Numbers clipTop() {
			return CanvasViewer.this.clipTop();
		}

		@Override
		public Numbers clipBottom() {
			return CanvasViewer.this.clipBottom();
		}

		@Override
		public Numbers clipLeft() {
			return CanvasViewer.this.clipLeft();
		}

		@Override
		public Numbers clipRight() {
			return CanvasViewer.this.clipRight();
		}

		public void destroy() {
			context.stroke();
		}
	}

	private void visitIntegral(Integral l) {
		selectColor(l);
		Area shape = new Area();
		l.visitSegments(new IntegralVisitor(l, shape, this));
		PathIterator iter = shape.getPathIterator(null);
		context.beginPath();
		while(!iter.isDone()) {
			float[] p = new float[6];
			switch( iter.currentSegment(p)) {
			case PathIterator.SEG_MOVETO: 
				context.moveTo(p[0], p[1]);
				break;
			case PathIterator.SEG_LINETO:
				context.lineTo(p[0], p[1]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
				break;
			}
			iter.next();
		}
		context.fill();

		DefaultAdapter.getDefault(l).put(Shape.class, shape);
	}

	public void visitMP(MP l) {
		selectColor(l);
		if(stroke != null) stroke.toStyle(context);
		trail = true;
		PathVisitor v = new PathVisitor();
		context.beginPath();
		l.visitSegments(v);
		v.destroy();
		trail = false;
	}

	@Override
	public void visitLocus(Locus l) {
		if(l instanceof Integral) {
			visitIntegral( (Integral) l);
		} else
		super.visitLocus(l);
	}

	public void visitFormule(Label label) {
		Align align = label.adapt(Align.class);
		if(align == null) align = Align.BASE;
		else if(align == Align.NONE) return;
		FormuleViewer viewer = new FormuleViewer(label.getString());
		FontStyle fs = label.adapt(FontStyle.class);
		if(fs != null) viewer.setFont(fs.getFont());
		else viewer.setFont(FONT_STYLE.getFont());
		double w = viewer.getWidth();
		double h = viewer.getHeight();
		double as = viewer.getAsHoogte();
		double x =  label.getXd();
		double y =  label.getYd();
		switch(align) {
		default: y -= as; break;
		case LEFT: x -= w;
		case RIGHT: y -= h/2.0; break;
		case TOP: y -= h;
		case BOTTOM: x -= w/2.0; break;
		}
		RectShape r = new RectShape(x, y, w, h);
		context.drawImage(viewer.getCanvas().getCanvasElement(), x, y, w, h);
		DefaultAdapter.getDefault(label).put(Shape.class, r);

	}
	private void visitFlipFlop(Label label) {
		boolean value = label.getState() != Label.FALSE;
		String off = "none";
		String on = "gray";
		float x = (float) label.getXd();
		float y = (float) label.getYd();
		if(value) {
			context.setFillStyle(on);
			context.fillRect(x, y, 10, 10);
		}
		selectColor(label);
		FONT_STYLE.toStyle(context);
		DEFAULT_STROKE.toStyle(context);
		context.strokeRect(x, y, 10, 10);
		String string = getMapper().toString(label);
		drawString(string, x+12, y+10, null, null, null);
		RectShape r = new RectShape(x, y, 12 + context.measureText(string).getWidth(), 10);
		DefaultAdapter.getDefault(label).put(Shape.class, r);
	}

	@Override
	public void visitLabel(Label label) {
		if(label.getRegistered() instanceof FlipFlop) {
			visitFlipFlop(label);
			return;
		}
		if (label.getString().contains("$")
		  ||Boolean.TRUE.equals(label.adapt(Boolean.class)))
		{
			visitFormule(label);
			return;
		}
		selectColor(label);
		String string = label.getString();
		double x = label.getXd();
		double y = label.getYd();
		Align align = label.adapt(Align.class);
		float extra = 0;
//zet het label correct tov het puntje.
		if(label.getRegistered() instanceof Interval) {
			try {
				if(Align.NONE == align) return;
				extra = 2;
				extra = label.getP().adapt(Float.class) / 2.0f; // NPE? 
			} catch (Exception e) {
			}
		}
		String h = null;
		String v = null;
		if(align != null) {
			switch(align) {
			case NONE: return;
			case LEFT:   h = TEXT_END;   v = TEXT_CENTRAL; break; 
			case RIGHT:  h = TEXT_START; v = TEXT_CENTRAL; break;
			case BOTTOM: h = TEXT_MIDDLE;v = TEXT_TOP; y+=extra;    break;
			case TOP:    h = TEXT_MIDDLE;v = TEXT_BOTTOM; y-=extra; break;
			default: 
			}
		} else align = Align.BASE;
		FontStyle fs = label.adapt(FontStyle.class);
		if (fs == null) fs = FONT_STYLE;
		
		fs.toStyle(context);
		drawString(string, x, y, h, v, null);
		FormuleFont ff = fs.getFont();
		double fontHeight = ff.getHeight();
		double ascent = ff.getAscent();
		RectShape rect = new RectShape(x, y, context.measureText(string).getWidth(), fontHeight);
		switch(align) {
		case LEFT:	rect.x -= rect.width; break; //als maxx > x dan x moet minder worden
		case BOTTOM: 
		case TOP:	rect.x -= rect.width/2; break;
		default:
		}
		switch(align) {
		case LEFT:	
		case RIGHT: rect.y -= rect.height/2; 
			break;
		case BASE:	rect.y -= ascent;break;
		case TOP:	rect.y -= rect.height; break;
		default:
		}
		DefaultAdapter.getDefault(label).put(Shape.class, rect);
	}

}
