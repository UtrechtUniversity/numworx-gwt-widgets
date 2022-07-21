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
import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefinergwt.client.ui.DashModel;
import nl.numworx.geodefinergwt.client.ui.LineModel;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;

public class DashHandler extends EventHandler {

	final private Map<String, Map<String, Object>> state;
	final private LineTypeCss css;
	final private DashModel model;

	public DashHandler(String string, Map<String,Map<String,Object>> state, LineTypeCss css, DashModel model) {
		super(string);
		this.state = state;
		this.css = css;
		this.model = model;
	}

	@Override
	public boolean allowSelection(Vector selection) {
		return !selection.isEmpty();
	}

	public void command() {
		Vector<Destroyable> selection = getModel().getSelect();
		Consumer<LineType> consumer = 
		(value -> {
			for(Destroyable p: selection) {
				float w = 1f;
				StrokeStyle s = p.adapt(StrokeStyle.class);
				if (s != null) w = (float) s.lineWidth;
				DefaultAdapter.getDefault(p).put(StrokeStyle.class, LineModel.getStroke(w,value));
				Map<String,Object> pstate = state.computeIfAbsent(getTracker().getMapper().toString(p), k -> new HashMap<>());
				pstate.put("type", value.name());
				p.forceChanged(IsLineType.LINE_TYPE);
			}
			getModel().clearSelection();
		});
		getLineType(consumer);
	}
		
	private void getLineType(Consumer<LineType> consumer) {
		PopupPanel panel = new PopupPanel(true, true);
		panel.setStylePrimaryName("MessageBox");
		LayoutPanel root = new LayoutPanel();
		root.setStyleName("dashes");
        String[] style = new String[] { css.SOLID(), css.DOTTED(), css.DASHED(), css.DASHDOTTED() };
        css.ensureInjected();
		panel.setWidget(root);
		int n = 0;
		for (int i = 0; i < 4; i++) {
			if (model.isSelected(i)) {
				FocusPanel p = new FocusPanel();
				LineType color = LineType.values()[i];
				p.setStyleName(style[i]);
				root.add(p);
				root.setWidgetTopHeight(p, 10+n*37, Unit.PX, 32, Unit.PX);
				root.setWidgetLeftWidth(p, 10+0*37, Unit.PX, 64+5, Unit.PX);
				p.addClickHandler(ev -> {
					consumer.accept(color);
					panel.hide();
					getTracker().paint();
				});
				n++;
			}
		}
		root.setPixelSize(2*37+15, n*37+15);
		panel.center();
	}

}
