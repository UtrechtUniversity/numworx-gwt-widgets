package nl.numworx.geodefinergwt.client.toolbox;

import java.util.Map;

import javax.inject.Inject;

import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class ZoomOutModel implements UIModel<Destroyable, Void> {
	@Inject ZoomOutModel() {}

	@Override
	public UIModel<Destroyable, Void> init(Destroyable item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UIModel<Destroyable, Void> init2(Destroyable d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void install() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> toMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromMap(ObjectMap value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Void editor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UIModel<Destroyable, Void> set(Tracker tracker) {
		return this;
	}

	@Override
	public void install(Destroyable buildPunt) {
		// TODO Auto-generated method stub
		
	}
}
