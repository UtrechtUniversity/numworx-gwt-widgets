package nl.numworx.geodefinergwt.client.ui;

import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.Context2d;

public class StrokeStyle implements SVGConstants {

	final String width;
	final String dasharray;
	final double lineWidth, dash[];

	StrokeStyle(String w, String d, double lw, double dash[]) {
		width = w;
		dasharray = d;
		lineWidth = lw;
		this.dash = dash;
	}

	StrokeStyle(String w, String d) {
		this(w,d,Double.parseDouble(w), null);
	}
	
	
	public void toStyle(OMSVGStyle style) {
		if (width != null)
			style.setSVGProperty(CSS_STROKE_WIDTH_PROPERTY, width);
		if (dasharray != null)
			style.setSVGProperty(CSS_STROKE_DASHARRAY_PROPERTY, dasharray);
	}

	public void toStyle(Context2d context) {
		context.setLineWidth(lineWidth);
		//context.setLineDash(dash);
	}
	
}
