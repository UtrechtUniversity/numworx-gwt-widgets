package nl.numworx.geodefinergwt.client;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.user.client.ui.Label;

import fi.euclides.event.EventHandler;
import fi.euclides.event.HitTester;
import fi.euclides.event.NameMapper;
import fi.euclides.event.Tracker;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Model;
import fi.euclides.model.Track;
import fi.euclides.proof.LabelDelegate;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.Randomizer;
import nl.numworx.geodefiner.common.math.Expression;

/**
 * Decorator pattern. Decorate with a NameMapper.
 * @author wim
 *
 */
@Singleton
public class TrackerImpl implements Tracker 
{
	Tracker viewer;
	NameMapper mapper;
	Map<String,LabelDelegate> register;
	Label status;
	Randomizer randomizer;
	
	@Inject TrackerImpl(AbstractViewer viewer, NamingModel mapper, @Named("status") Label status, Randomizer r, Expression e, Set<LabelDelegate> set) {
		super();
		this.viewer = viewer;
		this.mapper = mapper;
		this.status = status;
		this.randomizer = r;
		this.expression = e;
		this.register = new TreeMap<String,LabelDelegate>();
		for(LabelDelegate ld: set) ld.setTracker(this);
		e.setAllTracker(this);
	}

	private EventHandler pointerHandler;
	
	@Override
	public void setPointerHandler(EventHandler eventHandler) {
		pointerHandler = eventHandler;
		viewer.setPointerHandler(eventHandler);
	}

	public EventHandler getPointerHandler() {
		return pointerHandler;
	}

	@Override
	public void setStatus(String string) {
		status.setText(string);
	}

	@Override
	public Model getModel() {
		return viewer.getModel();
	}

	@Override
	public void paint() {
		viewer.paint();
	}

	@Override
	public boolean contains(double x, double y) {
		return viewer.contains(x, y);
	}

	@Override
	public String describe(Destroyable d) {
		return "";
	}

	@Override
	public NameMapper getMapper() {
		return mapper;
	}

	@Override
	public void register(String key, LabelDelegate delegate) {
		register.put(key, delegate);
	}

	@Override
	public LabelDelegate getRegistered(String key) {
		return register.get(key);
	}

	@Override
	public HitTester getHitTester() {
		return viewer.getHitTester();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T adapt(Class<T> cls) {
		if(fi.euclides.openmath.Expression.class == cls
				||nl.numworx.geodefiner.common.math.Expression.class == cls) 
			return (T) expression;
		if(Randomizer.class == cls) 
			return (T) randomizer;
		return viewer.adapt(cls);
	}

	fi.euclides.openmath.Expression expression;
}