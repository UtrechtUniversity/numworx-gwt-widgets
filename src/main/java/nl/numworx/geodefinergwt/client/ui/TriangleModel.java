package nl.numworx.geodefinergwt.client.ui;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Triangle;
import fi.euclides.util.DefaultAdapter;

public class TriangleModel extends CircleModel {

	private static final ColorStyle TRANSPARANT = new ColorStyle("rgba(0, 0, 0, 0)", 0);

	@Inject TriangleModel() {}

	@Override
	public void install(Destroyable item) {
		super.install(item);
		if (item instanceof Triangle) {
			DefaultAdapter.getDefault(item).put(ColorStyle.class, TRANSPARANT);
		}
	}

	@Override
	public void installLight() {
		super.installLight();
		if (item instanceof Triangle) {
			DefaultAdapter.getDefault(item).put(ColorStyle.class, TRANSPARANT);
		}
	}

}
