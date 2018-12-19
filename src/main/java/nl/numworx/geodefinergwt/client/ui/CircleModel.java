package nl.numworx.geodefinergwt.client.ui;

import fi.euclides.util.DefaultAdapter;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class CircleModel extends LineModel {
	static private final int TRANSPARANT = 0;
	private int fill = TRANSPARANT;

	@Override
	public void install() {
		super.install();
		FillStyle f;
		if(fill != TRANSPARANT)
			f = new FillStyle(fill);
		else f = new FillStyle("none",TRANSPARANT);
		DefaultAdapter.getDefault(item).put(f);
	}

	@Override
	public void fromMap(ObjectMap value) {
		super.fromMap(value);
		if(value.containsKey("fill"))
			fill = value.getInt("fill");
		else
			fill = TRANSPARANT;
	}
	
	
}
