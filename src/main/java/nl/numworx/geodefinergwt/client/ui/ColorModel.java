package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.Randomizer;
import nl.numworx.geodefiner.common.UIModel;
import nl.numworx.geodefinergwt.client.Tracer;
import nl.tue.win.riaca.openmath.lang.OMApplication;
import nl.tue.win.riaca.openmath.lang.OMObject;
import nl.tue.win.riaca.openmath.lang.OMSymbol;
import nl.tue.win.riaca.openmath.lang.OMVariable;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.json.ObjectMapImpl;
import fi.euclides.event.Tracker;
import fi.euclides.formuleobjects.FormuleParser;
import fi.euclides.formuleobjects.TokenMgrError;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.openmath.Expression;
import fi.euclides.util.DefaultAdapter;

public class ColorModel<T extends Destroyable> implements UIModel<T, Void> {

	protected Destroyable item;
	public int rgba = 0xFF000000;
	public boolean visible = true, trail,log;
	Tracker tracker;
	private Label visibility = new Label();
	
	@Override
	public UIModel<T, Void> init(T item) {
		this.item = item;
		return this;
	}
	
	private static final OMObject EUCLIDES_VISIBLE = new OMSymbol("euclides", "visible");
	
	public void installLight() {
		ColorStyle css = new ColorStyle(rgba);
		DefaultAdapter.getDefault(item).put(css);
	}
	
	
	@Override
	public void install(Destroyable item) {
		//java.util.logging.Logger.getLogger("ColorModel").info("install " + item + " " + rgba);
		ColorStyle css = new ColorStyle(rgba);
		DefaultAdapter.getDefault(item).put(css);
		item.setVisible(visible);
        if(trail && tracker != null) {
          tracker.getModel().startTrail(item);
        }
        if (log && tracker != null) {
          Tracer observer = tracker.adapt(Tracer.class);
          if (observer != null) {
            item.addObserver(observer);
            DefaultAdapter.getDefault(item).put(observer);
          }
          
        }
        
		if (visibility.getString() != null && tracker != null) {
			try {
				String formula = visibility.getString();
				final String orig = formula; 
				Randomizer r = tracker.adapt(Randomizer.class);
				if(r != null) formula = r.randomize(formula);
				formula = formula.substring(2);
				visibility.destroy(); visibility = new Label();
				visibility.setString(orig);
				OMObject o = new FormuleParser(formula).logic();
				OMApplication oma = new OMApplication();
				oma.addElement(EUCLIDES_VISIBLE);
				oma.addElement(new OMVariable(tracker.getMapper().toString(item)));
				oma.addElement(o);
				Expression expr = tracker.adapt(Expression.class);
				Destroyable v = expr.interpret(oma, visibility, tracker.getMapper());
				v.setVisible(false);
				tracker.getModel().add(v);
			} catch (Exception e) {
			} catch (TokenMgrError tme) {
			}
		}
	}


	@Override
	public Map<String, Object> toMap() {
		if (objectMap != null)
			return toMap(objectMap);
		if (item == null) return null;
		String name = tracker.getMapper().toString(item);
		Map<String, Object> m = UIModelFactoryGWT.configuration.getMap(name);
		return m;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> toMap(ObjectMap map) {
		if (map instanceof ObjectMapImpl) {
			return (Map<String, Object>) ((ObjectMapImpl) map).unwrap();
		}
		if(map instanceof Map)
			return (Map<String,Object>)map;
		
		return null;
	}

	private ObjectMap objectMap;
	@Override
	public void fromMap(ObjectMap value) {
		objectMap = value;
		rgba = value.getInt("color");
		visible = value.getBoolean("visible", true);
		visibility.setString(value.getString("visibility"));
		trail = value.getBoolean("trail", false);
		log = value.getBoolean("log",false);
	}

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


	@Override
	final public void install() {
		install(item);
		
	}

}
