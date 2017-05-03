package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.UIModel;
import nl.numworx.geodefiner.common.Volgpunt;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.DefaultAdapter;

public class TextModel extends ColorModel<Label> {
	public Align align = Align.BASE;
	private FontStyle font = new FontStyle();
	private float dx, dy;
	Boolean alwaysF;
	Label item;
	
	@Override
	public void install() {
		DefaultAdapter.getDefault(item).put(align);
		DefaultAdapter.getDefault(item).put(font);
		DefaultAdapter.getDefault(item).put(Boolean.class, alwaysF);
		Punt p = item.getP();
		if(p instanceof Volgpunt) {
			((Volgpunt) p).setDxy(Numbers.createDouble(dx), Numbers.createDouble(dy));
		}
		super.install();
	}

	public UIModel<Label, Void> init(Label item) {
		align = item.adapt(Align.class);
		if(align == null) align= Align.BASE;
		this.item = item;
		super.init(item);
		return this;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("align", align.name());
		map.put("font", font.toMap());
		if( Boolean.TRUE.equals(alwaysF))
			map.put("alwaysF", alwaysF);
		return map;
	}

	@Override
	public void fromMap(ObjectMap map) {
		try {
			align = Align.valueOf(map.getString("align"));
		} catch (Exception e) {
			align = Align.BASE;
		}
		if(map.containsKey("dx")) {
			dx = (float)map.getDouble("dx");
		}
		if(map.containsKey("dy")) {
			dy = (float)map.getDouble("dy");
		}
		if (map.containsKey("font")) {
			font.fromMap(map.getObjectMap("font"));
		}
		alwaysF = map.getBoolean("alwaysF", false);
		super.fromMap(map);
	}

}
