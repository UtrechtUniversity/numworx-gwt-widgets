package nl.numworx.geodefinergwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

class FormuleCache implements Observer {

	final Label item;
	String string;
	private FormuleViewer viewer;
	private double w, h, as;
	private CanvasElement element;
	
	boolean isValid() {
		return string != null;
	}
	
	@Override
	public void update(Observable observable, Object arg) {
		if(arg == Label.DESTROY) {
			string = null;
		}
		if (observable == item) {
			if(! item.getString().equals(string))	
				item.deleteObserver(this);
				string = null; // Invalidate.
				//DefaultAdapter.getDefault(item).put(FormuleCache.class, null);
				Context2d ctx = viewer.getCanvas().getContext2d();
				ctx.setStrokeStyle("#CCC");
				ctx.strokeRect(1, 1, w-2, h-2);
		}		
	}

	FormuleCache(Label item) {
		this.item = item;
		this.string = item.getString();
		viewer = new FormuleViewer(string);
		FontStyle fs = item.adapt(FontStyle.class);
		if(fs != null) viewer.setFont(fs.getFont());
		else viewer.setFont(CanvasViewer.FONT_STYLE.getFont());
		w = viewer.getWidth();
		h = viewer.getHeight();
		as = viewer.getAsHoogte();
		element = viewer.getCanvas().getCanvasElement();
		item.addObserver(this);
		DefaultAdapter.getDefault(item).put(this);
	}

	double getW() {
		return w;
	}

	double getH() {
		return h;
	}

	double getAs() {
		return as;
	}

	CanvasElement getElement() {
		return element;
	}
	
	String toDataUrl() {
		return viewer.getCanvas().toDataUrl();
	}
	
}
