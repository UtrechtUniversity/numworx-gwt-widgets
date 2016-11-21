package nl.numworx.geodefinergwt.client;

import java.awt.FontMetrics;

import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import nl.numworx.geodefiner.common.Align;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;

import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.gwt.svg.SVGWidget;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.util.Adapter;

public class InstanceViewer extends SVGWidget {

	public InstanceViewer(int width, int height) {
		super(width, height);
	}

	public void selectColor(Destroyable object) {
		if(tracking || trail)
			return;
		Adapter a = object.getAdapter();
//		Stroke stroke = a.adapt(Stroke.class);
//		if(stroke == null) stroke = DEFAULT_STROKE;
//		g.setStroke(stroke);

		CssColor c = a.adapt(CssColor.class);
		if (c != null) {
			// if selected?
			setCssColor(c);
			return;
		}
		super.selectColor(object);
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


}
