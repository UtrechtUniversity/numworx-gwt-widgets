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
import nl.numworx.geodefiner.common.Integral;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;
import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.euclides.gwt.svg.SVGRectShape;
import fi.euclides.gwt.svg.SVGWidget;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Locus;
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

public class InstanceViewer extends SVGWidget {

	private static final float DEFAULT_POINTSIZE = 5;
	private AnimationHandle animator;

	public InstanceViewer() {	
	}
	public InstanceViewer(int width, int height) {
		init(width, height);
	}

	private StrokeStyle stroke;
	
	public void selectColor(Destroyable object) {
		if(tracking || trail)
			return;
		Adapter a = object.getAdapter();
		stroke = a.adapt(StrokeStyle.class);

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
	protected void drawLine(double x1, double y1, double x2, double y2) {
		OMSVGLineElement line = doc.createSVGLineElement((float)x1, (float)y1, (float)x2, (float)y2);
		OMSVGStyle style = line.getStyle();
		style.setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
		if(stroke != null) stroke.toStyle(style);
		getBody().appendChild(line);
	}

	
	public void visitFormule(Label label) {
		FormuleViewer viewer = new FormuleViewer(label.getString());
		String url = viewer.getCanvas().toDataUrl();
		float w = viewer.getWidth();
		float h = viewer.getHeight();
		float as = viewer.getAsHoogte();
		float x = (float) label.getXd();
		float y = (float) label.getYd();
		Align align = label.adapt(Align.class);
		if(align == null) align = Align.BASE;
		switch(align) {
		case BASE: y -= as; break;
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
		OMSVGTextElement text = doc.createSVGTextElement(x+12, y+10, OMSVGLength.SVG_LENGTHTYPE_NUMBER,string);
		g.appendChild(rect);
		g.appendChild(text);
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
		if(label.getString().contains("$"))
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
			case LEFT:   h = "end";   	v = "central";          break; 
			case RIGHT:  h = "start";   v = "central";          break;
			case TOP:    h = "middle";  v = "text-before-edge"; break;
			case BOTTOM: h = "middle";  v = "text-after-edge";  break;
			case BASE: 
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
		case RIGHT: x += x - bbox.getX(); break;
		case BOTTOM: 
		case TOP:	x += x = bbox.getCenterX(); break;
		}
// This is how to position after bbox
		OMSVGLength newX = doc.getRootElement().createSVGLength(unitType, (float) x);
		text.getX().getBaseVal().replaceItem(newX, 0);

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
	}

	private void visitIntegral(Integral l) {
		selectColor(l);
		final double y0;

		if(l.base > 0) y0 = clipTop().doubleValue();
		else if( l.base < 0) y0 = clipBottom().doubleValue();
		else y0 = getModel().getO().getYd();

		final Area shape = new Area();
		l.visitSegments(new SegmentVisitor() {

			@Override
			public void visitSegment(Segment s) {
				double x1 = s.getX1();
				double x2 = s.getX2();
				double y1 = s.getY1();
				double y2 = s.getY2();
				Path2D.Double path = new Path2D.Double();
				path.moveTo(x1, y0);
				path.lineTo(x1, y1);
				path.lineTo(x2, y2);
				path.lineTo(x2, y0);
				path.closePath();
				Area area = new Area(path);
				shape.add(area);
			}

			@Override
			public Numbers clipTop() {
				return Numbers.createDouble(Double.NEGATIVE_INFINITY);
			}

			@Override
			public Numbers clipBottom() {
				return Numbers.createDouble(Double.POSITIVE_INFINITY);
			}

			@Override
			public Numbers clipLeft() {
				return InstanceViewer.this.clipLeft();
			}

			@Override
			public Numbers clipRight() {
				return InstanceViewer.this.clipRight();
			} });
//g.fill(shape);
		PathIterator iter = shape.getPathIterator(null);
		OMSVGPathElement path = doc.createSVGPathElement();
		OMSVGPathSegList points = path.getPathSegList();
		while(!iter.isDone()) {
			iter.next();
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

}
