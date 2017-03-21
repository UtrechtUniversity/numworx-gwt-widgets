package nl.numworx.geodefinergwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

class FormuleCache implements Observer {

	static class CacheHolder extends FormuleHolder {
		private Context2d ctx;
		private Canvas    canvas;

		CacheHolder(FormuleFont font, CssColor color, Canvas canvas) {
			super(font, color);
			this.canvas = canvas;
			this.ctx = canvas.getContext2d();
		}

		@Override
		public Canvas createCanvas(FormuleElement element) {
			return null;
		}

		@Override
		public Context2d createContext2d(FormuleElement element) {
			return null;
		}

		@Override
		public double measureWidth(FormuleElement element, FormuleFont f,
				String string) {
			ctx.setFont(f.getFontStyle());
			return ctx.measureText(string).getWidth();
		}

		@Override
		public Canvas getCanvas() {
			return canvas;
		}
		
	}

	final Label item;
	String string;
	private CacheHolder viewer;
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
		Canvas canvas = Canvas.createIfSupported();
		Context2d ctx = canvas.getContext2d();
		viewer = new CacheHolder(ff, cc, canvas);
		//viewer = new FormuleHolder(); viewer.setFont(ff); viewer.setColor(cc);
		viewer.getMainRegel().insert(trim(string));
		viewer.getMainRegel().validate();
		w = viewer.getWidth();
		h = viewer.getHeight();
		double ratio = 1.0; // getDeviceRatio(ctx);
		canvas.setPixelSize((int)w, (int)h);
		if(ratio > 1.0) {
			canvas.setCoordinateSpaceHeight((int) (h*ratio));
			canvas.setCoordinateSpaceWidth((int) (w*ratio));
			ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
		} else {
		//change the canvas dimensions
			canvas.setCoordinateSpaceHeight((int)h);
			canvas.setCoordinateSpaceWidth((int)w);
		}
		//ctx.clearRect(0, 0, w, h); // uitpoetsen als setPixelSize dat niet gedaan heeft.
		
		//viewer.getMainRegel().paintAll(viewer.getCanvas().getContext2d());
		viewer.getMainRegel().paintAll(ctx);
		as = viewer.getMainRegel().getAsHoogte();
		element = canvas.getCanvasElement();
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
