package nl.numworx.geodefinergwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import nl.numworx.geodefiner.common.Randomizer;
import nl.numworx.geodefiner.common.UIModel;
import nl.numworx.geodefiner.common.math.Expression;
import nl.tue.win.riaca.openmath.lang.OMApplication;
import nl.tue.win.riaca.openmath.lang.OMObject;
import nl.tue.win.riaca.openmath.lang.OMSymbol;
import nl.tue.win.riaca.openmath.lang.OMVariable;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.event.Tracker;
import fi.euclides.formuleobjects.FormuleParser;
import fi.euclides.formuleobjects.TokenMgrError;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;

public class ColorModel<T extends Destroyable> implements UIModel<T, Void> {

	protected Destroyable item;
	public int rgba = 0xFF000000;
	public boolean visible = true;
	Tracker tracker;
	private Label visibility = new Label();
	
	@Override
	public UIModel<T, Void> init(T item) {
		this.item = item;
		return this;
	}
	
	private static final OMObject EUCLIDES_VISIBLE = new OMSymbol("euclides", "visible");
	
	@Override
	public void install() {
		//java.util.logging.Logger.getLogger("ColorModel").info("install " + item + " " + rgba);
		ColorStyle css = new ColorStyle(colorString(rgba));
		DefaultAdapter.getDefault(item).put(css);
		item.setVisible(visible);
		if (visibility.getString() != null && tracker != null) {
			try {
				String formula = visibility.getString();
				Randomizer r = tracker.adapt(Randomizer.class);
				if(r != null) formula = r.randomize(formula);
				formula = formula.substring(2);
				OMObject o = new FormuleParser(formula).logic();
				OMApplication oma = new OMApplication();
				oma.addElement(EUCLIDES_VISIBLE);
				oma.addElement(new OMVariable(tracker.getMapper().toString(item)));
				oma.addElement(o);
				fi.euclides.openmath.Expression expr;
				expr = tracker.adapt(fi.euclides.openmath.Expression.class);
				visibility.destroy();
				Destroyable v = expr.interpret(oma, visibility, tracker.getMapper());
				v.setVisible(false);
				tracker.getModel().add(v);
			
			} catch (Exception e) {
			} catch (TokenMgrError tme) {
			}
		}
	}

	protected String colorString(int rgba) {
		int a = (rgba >> 24) & 0xFF;
		if( a < 0xFF ) {
			return
				"rgba("
				+ ((rgba >> 16 ) & 0xFF)
				+ ','
				+ ((rgba >> 8 ) & 0xFF)
				+ ','
				+ ( rgba & 0xFF)
				+ ','
				+ ( a / 255.0f)
				+ ')';
		}
		String hex = Integer.toHexString(rgba&0xFFFFFF).toUpperCase();
		hex = "00000" + hex;
		int l = hex.length();
		hex = hex.substring(l-6);
		return "#" + hex;
	}

	@Override
	public Map<String, Object> toMap() {
		return null;
	}

	@Override
	public void fromMap(ObjectMap value) {
		rgba = value.getInt("color");
		visible = value.getBoolean("visible", true);
		visibility.setString(value.getString("visibility"));	}

	@Override
	public Void editor() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public UIModel<T, Void> set(Tracker tracker) {
		this.tracker = tracker;
		return this;
	}

	@Override
	public UIModel<T, Void> init2(Destroyable item) {
		this.item = item;
		return this;
	}

}
