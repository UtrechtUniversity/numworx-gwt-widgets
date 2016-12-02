package nl.numworx.geodefinergwt.client;

import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;

import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.gwt.svg.SVGWidget;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.Triangle;
import fi.euclides.util.Adapter;

public class InstanceViewer extends SVGWidget {

	private static final float DEFAULT_POINTSIZE = 5;
	private AnimationHandle animator;

	public InstanceViewer(int width, int height) {
		super(width, height);
	}

	private StrokeStyle stroke;
	
	public void selectColor(Destroyable object) {
		if(tracking || trail)
			return;
		Adapter a = object.getAdapter();
		stroke = a.adapt(StrokeStyle.class);
		CssColor c = a.adapt(CssColor.class);
		if (c != null) {
			// if selected?
			setCssColor(c);
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
		getSvgElement().appendChild(line);
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
		getSvgElement().appendChild(image);
	}
	
	
	@Override
	public void visitLabel(Label label) {
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
			case LEFT:   h = "start";   v = "central";          break; 
			case RIGHT:  h = "end";     v = "central";          break;
			case TOP:    h = "center";  v = "text-before-edge"; break;
			case BOTTOM: h = "center";  v = "text-after-edge";  break;
			case BASE: 
			}
		}
		short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
		OMSVGTextElement text = doc.createSVGTextElement((float)x, (float)y, unitType, string);
		OMSVGStyle style = text.getStyle();
		GWT.log("color for" + label + " is " + color);
		//color = "blue";
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
		if(h != null) style.setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, h);
		if(v != null) style.setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, v);
		getSvgElement().appendChild(text);
	}

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
		style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
		style.setSVGProperty(SVGConstants.CSS_FILL_RULE_PROPERTY, SVGConstants.CSS_EVENODD_VALUE);
		getSvgElement().appendChild(path);
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


}
