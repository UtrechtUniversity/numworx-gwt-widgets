package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;

public class TextModel extends ColorModel<Label> {
	public Align align = Align.BASE;

	@Override
	public void install() {
		DefaultAdapter.getDefault(item).put(align);
		super.install();
	}

	public UIModel<Label, Void> init(Label item) {
		align = item.adapt(Align.class);
		if(align == null) align= Align.BASE;
		super.init(item);
		return this;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("align", align.name());
		return map;
	}

	@Override
	public void fromMap(ObjectMap map) {
		try {
			align = Align.valueOf(map.getString("align"));
		} catch (Exception e) {
			align = Align.BASE;
		}
		super.fromMap(map);
	}

}
