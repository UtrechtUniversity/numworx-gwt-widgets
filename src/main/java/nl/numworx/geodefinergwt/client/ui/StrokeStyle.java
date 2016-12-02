package nl.numworx.geodefinergwt.client.ui;

import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.utils.SVGConstants;

public class StrokeStyle implements SVGConstants {

	final String width;
	final String dasharray;

	StrokeStyle(String w, String d) {
		width = w;
		dasharray = d;
	}

	public void toStyle(OMSVGStyle style) {
		if (width != null)
			style.setSVGProperty(CSS_STROKE_WIDTH_PROPERTY, width);
		if (dasharray != null)
			style.setSVGProperty(CSS_STROKE_DASHARRAY_PROPERTY, dasharray);
	}
	
}
