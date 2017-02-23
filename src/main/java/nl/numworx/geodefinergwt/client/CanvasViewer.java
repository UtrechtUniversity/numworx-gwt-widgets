package nl.numworx.geodefinergwt.client;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;

import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Triangle;
import fi.euclides.util.Adapter;
import fi.euclides.util.DefaultAdapter;
import gwt.awt.Shape;
import gwt.awt.geom.Path2D;

public class CanvasViewer extends SpeelVeld implements SnapperImpl.PH {

	private AnimationHandle animator;

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
		context.setFillStyle(fill);
		context.fill();
		if(stroke != null) stroke.toStyle(context);
		context.stroke();
		DefaultAdapter.getDefault(t).put(Shape.class, path);
	}

	public void selectColor(Destroyable object) {
		if(tracking || trail)
			return;
		Adapter a = object.getAdapter();
		stroke = a.adapt(StrokeStyle.class);
		context.setLineWidth(1);
		//context.setLineDash(null)
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
		case ATSTART: tip(s.getP1(), dx, dy);
		case NOTIP: 
		}
		return s;
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

	
	
}
