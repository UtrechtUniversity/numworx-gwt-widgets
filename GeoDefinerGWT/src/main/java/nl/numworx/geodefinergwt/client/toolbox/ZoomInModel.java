package nl.numworx.geodefinergwt.client.toolbox;

import java.util.Map;

import javax.inject.Inject;

import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class ZoomInModel implements UIModel<Destroyable, Void> {

	@Inject ZoomInModel() {}
	
	double magnification = 2;
	
	
	@Override
	public UIModel<Destroyable, Void> init(Destroyable item) {
		return this;
	}

	@Override
	public UIModel<Destroyable, Void> init2(Destroyable d) {
		return this;
	}

	@Override
	public void install() {
	}

	@Override
	public Map<String, Object> toMap() {
		return null;
	}

	@Override
	public void fromMap(ObjectMap value) {
		if (value.containsKey("magnification")) {
			magnification = value.getDouble("magnification");
		}
		
	}

	@Override
	public Void editor() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
	}

	@Override
	public UIModel<Destroyable, Void> set(Tracker tracker) {
		return this;
	}

	@Override
	public void install(Destroyable d) {
	}

}
