package nl.numworx.geodefinergwt.client;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;

import nl.numworx.geodefiner.common.Snapper;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.Punt;
import fi.euclides.model.Triangle;
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

	String fill = "white";
	
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
		context.stroke();
		DefaultAdapter.getDefault(t).put(Shape.class, path);
	}

}
