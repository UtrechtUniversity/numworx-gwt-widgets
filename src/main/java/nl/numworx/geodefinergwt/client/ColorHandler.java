package nl.numworx.geodefinergwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.euclides.event.EventHandler;
import fi.euclides.model.Destroyable;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;

public class ColorHandler extends EventHandler {

	final private Map<String, Map<String, Object>> state;


	public ColorHandler(String string, Map<String,Map<String,Object>> state) {
		super(string);
		this.state = state;
	}

	@Override
	public boolean allowSelection(@SuppressWarnings("rawtypes") Vector selection) {
		return !selection.isEmpty();
	}

	public void command() {
		Vector<Destroyable> selection = getModel().getSelect();
		Consumer<ColorStyle> consumer = 
		(value -> {
			for(Destroyable p: selection) {
				ColorStyle c = p.adapt(ColorStyle.class);
				int rgba = 0xFF000000;
				if (c != null) rgba = c.getRGB();
				Map<String,Object> pstate = state.computeIfAbsent(getTracker().getMapper().toString(p), k -> new HashMap<>());
				if ( (rgba & 0xFF000000) == 0xFF000000)
				{	
					DefaultAdapter.getDefault(p).put(ColorStyle.class, value);
					pstate.put("color", value.getRGB());
				} else {
					rgba = rgba | 0xFFFFFF;
					rgba = rgba & value.getRGB();
					DefaultAdapter.getDefault(p).put(ColorStyle.class, new ColorStyle(rgba));
					pstate.put("color", rgba);
				}
				FillStyle f = p.adapt(FillStyle.class);
				if ( f != null && (f.getRGB()&0xFF000000) != 0) {
					int a = f.getRGB()|0xFFFFFF;
					a = a & value.getRGB();
					DefaultAdapter.getDefault(p).put(new FillStyle(a));
					pstate.put("fill", a);
				}
				// shortcut
				IsColor iscolor = p.adapt(IsColor.class);
				if (iscolor != null) iscolor.updateColor();				
			}
			getModel().clearSelection();
		});
		getColor(consumer);
	}
	
	static int rgb(int r, int g, int b) {
		return r << 16|g<<8|b|0xFF000000;
	}
	static int rgb(int rgb) { 
		return 0xFF000000 | (rgb & 0xFFFFFF);
	}
	
	static final ColorStyle colors[]= {
			new ColorStyle(rgb(0x7fcc99 /*groen*/)),
			new ColorStyle(rgb(0x7ddfff /*blauw*/)),
			new ColorStyle(rgb(0xff7f7f /*rood*/)),
			new ColorStyle(rgb(0xffe67f /*geel*/)),
			new ColorStyle(rgb(0xc97dff /*paars*/)),
			new ColorStyle(rgb(100,100,100)),
			new ColorStyle(rgb(0,0,0)),
			new ColorStyle(rgb(180,180,180)),
	};
	
	
	private void getColor(Consumer<ColorStyle> consumer) {
		PopupPanel panel = new PopupPanel(true, true);
		panel.setStylePrimaryName("MessageBox");
		LayoutPanel root = new LayoutPanel();
		root.setStyleName("colors");
		panel.setWidget(root);
		root.setPixelSize(4*37+15, 2*37+15);
		for (int i = 0; i < 8; i++) {
			int k = i / 4;
			int j = i % 4;
			FocusPanel p = new FocusPanel();
			ColorStyle color = colors[i];
			p.getElement().getStyle().setBackgroundColor(color.getColor());
			root.add(p);
			root.setWidgetTopHeight(p, 10+k*37, Unit.PX, 32, Unit.PX);
			root.setWidgetLeftWidth(p, 10+j*37, Unit.PX, 32, Unit.PX);
			p.addClickHandler(ev -> {
				consumer.accept(color);
				panel.hide();
				getTracker().paint();
			});
		}
		panel.center();
	}

}
