package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class AxesModel extends LineModel {

	public boolean numbers;

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		if(numbers) map.put("numbers", Boolean.TRUE);
		return map;
	}

	@Override
	public void fromMap(ObjectMap map) {
		numbers = map.getBoolean("numbers", false);
		super.fromMap(map);
	}

}
