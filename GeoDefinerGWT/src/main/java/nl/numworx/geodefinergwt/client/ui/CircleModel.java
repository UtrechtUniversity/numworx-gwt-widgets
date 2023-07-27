package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.util.DefaultAdapter;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class CircleModel extends LineModel {
	static private final int TRANSPARANT = 0;
	private int fill = TRANSPARANT;

	@Override
	public void install(Destroyable item) {
		FillStyle f;
		if(fill != TRANSPARANT)
			f = new FillStyle(fill);
		else f = new FillStyle("none",TRANSPARANT);
		DefaultAdapter.getDefault(item).put(f);
		super.install(item);
	}

	@Override
	public void fromMap(ObjectMap value) {
		super.fromMap(value);
		if(value.containsKey("fill"))
			fill = value.getInt("fill");
		else
			fill = TRANSPARANT;
	}
	
	@Inject public CircleModel() { }

	@Override
	public void installLight() {
		FillStyle f;
		if(fill != TRANSPARANT)
			f = new FillStyle(fill);
		else f = new FillStyle("none",TRANSPARANT);
		DefaultAdapter.getDefault(item).put(f);
		super.installLight();
	}
	
	@Override
	public void fromLightMap(ObjectMap value) {
		super.fromLightMap(value);
		if (value.containsKey("fill")) 
			fill = value.getInt("fill");
		else {
			FillStyle f = item.adapt(FillStyle.class);
			if (f != null) {
				fill = f.getRGB();
			} else {
				fill = TRANSPARANT;
			}
		}
	}
	
	@Override
	public Map<String, Object> toLightMap() {
		Map<String,Object> m = super.toLightMap();
		m.put("fill", fill);
		return m;
	}
	
}
