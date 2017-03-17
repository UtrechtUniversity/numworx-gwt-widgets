package nl.numworx.geodefinergwt.client;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

class FormuleCache implements Observer {

	final Label item;
	String string;
	private FormuleHolder viewer;
	private double w, h, as;
	private CanvasElement element;
	private FillStrokeStyle css;
	
	boolean isValid() {
		return string != null && 
			string.equals(item.getString());
	}
	
	@Override
	public void update(Observable observable, Object arg) {
		if(arg == Label.DESTROY) {
			string = null;
		}
		if (observable == item) {
			if(! item.getString().equals(string))	
			{
				destroy();
		}}		
	}

	FormuleCache(Label item, FillStrokeStyle css) {
		this.item = item;
		this.string = item.getString();
		this.css = css;		
		FontStyle fs = item.adapt(FontStyle.class);
		FormuleFont ff;
		if(fs != null) ff = fs.getFont();
		else ff = CanvasViewer.FONT_STYLE.getFont();
		CssColor cc = css.cast();
		//viewer = new FormuleHolder(ff, cc);
		viewer = new FormuleHolder(); viewer.setFont(ff); viewer.setColor(cc);
		viewer.getMainRegel().insert(trim(string));
		viewer.paint();
		w = viewer.getWidth();
		h = viewer.getHeight();
		as = viewer.getMainRegel().getAsHoogte();
		element = viewer.getCanvas().getCanvasElement();
		item.addObserver(this);
		DefaultAdapter.getDefault(item).put(this);
	}

	FillStrokeStyle getCss() {
		return css;
	}

	private String trim(String string) {
		if(string.startsWith("$f")) {
			return string.substring(2, string.length()-1);
		}
		return string;
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

	public boolean isValid(FillStrokeStyle fillStyle) {
		return isValid() && fillStyle .equals( css );
	}

	public void destroy() {
		item.deleteObserver(this);
		string = null; // Invalidate.
		//DefaultAdapter.getDefault(item).put(FormuleCache.class, null);
		Context2d ctx = viewer.getCanvas().getContext2d();
		ctx.setStrokeStyle(css);
		ctx.strokeRect(1, 1, w-2, h-2);
	}
	
}
