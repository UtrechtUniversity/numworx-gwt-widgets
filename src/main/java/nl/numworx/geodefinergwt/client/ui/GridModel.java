package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Locus;

public class GridModel extends LineModel {
	boolean gravity;

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("gravity", gravity);
		return map;
	}

	@Override
	public void fromMap(ObjectMap map) {
		gravity = map.getBoolean("gravity", false);
		super.fromMap(map);
	}

	@Override
	public void install() {
		super.install();
		Snapper snapper = tracker.adapt(Snapper.class);
		snapper.setGravity(gravity);
	}
	
	public UIModel<Destroyable, Void> init(Locus item) {
		rgba=0xFF808080;
		type = LineType.DOTTED;
		return super.init(item);
	}

}
