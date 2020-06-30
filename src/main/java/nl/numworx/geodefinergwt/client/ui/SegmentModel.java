package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Lijn;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class SegmentModel extends LineModel {

	Tips tip = Tips.NOTIP;
	
//	@Override
//	public Map<String, Object> toMap() {
//		Map<String, Object> map = super.toMap();
////		if(tip != Tips.NOTIP) map.put("tip", tip.name());
//		return map;
//	}

	@Override
	public void fromMap(ObjectMap map) {
		try { 
			tip = Tips.valueOf(map.getString("tip"));
		} catch (Exception e) {
			tip = Tips.NOTIP;
		}
		super.fromMap(map);
	}

	@Override
	public void install(Destroyable item) {
		super.install(item);
		DefaultAdapter adapter = DefaultAdapter.getDefault(item);
		if(tip == Tips.NOTIP) {
			adapter.put(Tips.class, null);
			adapter.put(Float.class, null);
		} else {
			adapter.put(tip);
			adapter.put(width);
		}
	}
	@Inject public SegmentModel() {}

	@Override
	public void installLight() {
		DefaultAdapter adapter = DefaultAdapter.getDefault(item);
		if(tip == Tips.NOTIP) {
			adapter.put(Tips.class, null);
			adapter.put(Float.class, null);
		} else {
			adapter.put(tip);
			adapter.put(width);
		}
		super.installLight();
	}
}
