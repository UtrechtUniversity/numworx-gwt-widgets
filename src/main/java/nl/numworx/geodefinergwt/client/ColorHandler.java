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
	public boolean allowSelection(Vector selection) {
		return !selection.isEmpty();
	}

	public void command() {
		Vector<Destroyable> selection = getModel().getSelect();
		Consumer<ColorStyle> consumer = 
		(value -> {
			for(Destroyable p: selection) {
				DefaultAdapter.getDefault(p).put(ColorStyle.class, value);
				Map<String,Object> pstate = state.computeIfAbsent(getTracker().getMapper().toString(p), k -> new HashMap<>());
				pstate.put("color", value.getRGB());
				
				FillStyle f = p.adapt(FillStyle.class);
				if ( f != null && (f.getRGB()&0xFF000000) != 0) {
					int a = f.getRGB()|0xFFFFFF;
					a = a & value.getRGB();
					DefaultAdapter.getDefault(p).put(new FillStyle(a));
					pstate.put("fill", a);
				}
				
				
			}
			getModel().clearSelection();
		});
		getColor(consumer);
	}
	
	static int rgb(int r, int g, int b) {
		return r << 16|g<<8|b|0xFF000000;
	}
	
	ColorStyle colors[]= {
			new ColorStyle(rgb(69,123,59)),
			new ColorStyle(rgb(49,100,186)),
			new ColorStyle(rgb(194,62,56)),
			new ColorStyle(rgb(204,104,46)),
			new ColorStyle(rgb(99,89,203)),
			new ColorStyle(rgb(100,100,100)),
			new ColorStyle(rgb(0,0,0)),
			new ColorStyle(rgb(180,180,180)),
	};
	
	
	private void getColor(Consumer<ColorStyle> consumer) {
		PopupPanel panel = new PopupPanel(true, true);
		LayoutPanel root = new LayoutPanel();
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
