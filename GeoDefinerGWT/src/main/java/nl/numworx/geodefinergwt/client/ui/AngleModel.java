package nl.numworx.geodefinergwt.client.ui;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.proof.AngleType;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class AngleModel extends TextModel {
	
	private AngleType rad;

	@Inject AngleModel() {
	}

	private void inject(Destroyable d) {
		DefaultAdapter.getDefault(d).put(AngleType.class, rad);
		if (d instanceof Label) {
			((Label) d).getRegistered().update(d, null);
		}
	}
	
	@Override
	public void install(Destroyable item) {
		inject(item);
		super.install(item);
	}

	@Override
	public void installLight() {
		inject(item);
		super.installLight();
	}

	@Override
	public UIModel<Label, Void> init(Label item) {
		if(item != null) rad = item.adapt(AngleType.class);
		return super.init(item);
	}

	@Override
	public void fromMap(ObjectMap map) {
		if( ! map.getBoolean("rad", true)) rad = AngleType.DEGREE;		
		super.fromMap(map);
	}

}
