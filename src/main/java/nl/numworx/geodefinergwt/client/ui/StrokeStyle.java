package nl.numworx.geodefinergwt.client.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

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

	public StrokeStyle( double lw, double dash[]) {
		width = null;
		dasharray = null;
		lineWidth = lw;
		this.dash = dash;
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
		try {
			setLineDash(context, dash);
		} catch (Exception e) {
			Logger.getLogger("StrokeStyle").log(Level.WARNING, "unsupported setLineDash", e);
		}
	}

	/**
	 * Chrome and Firefox 33+ support setLineDash()
	 * Older Firefox version support only mozDash()
	 */

	private final native void setLineDash(Context2d ctx, double[] dash) 
	throws Exception
	/*-{
		if (ctx.setLineDash !== undefined) {
			if (dash != null) {
				ctx.setLineDash(dash);
			} else {
				ctx.setLineDash([]); // Firefox 33+ on Linux dont show solid lines if ctx.setLineDash([0]) is used, therefore use empty array which works on every browser
			}
		} else if (ctx.mozDash !== undefined) {
			if (dash != null) {
				ctx.mozDash = dash;
			} else { // default is null
				ctx.mozDash = null;
			}
		} else if (dash != null) { // if another line than a solid one should be set and the browser doesn't support it throw an Exception
			throw new Exception();
		}
	}-*/;

}
