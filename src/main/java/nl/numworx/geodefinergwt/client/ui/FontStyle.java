package nl.numworx.geodefinergwt.client.ui;

import java.util.Collections;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

public class FontStyle {
	float size = 12;
	
	public void toStyle(Style style) {
		style.setFontSize(size, Unit.PX);
	}

	void fromMap(ObjectMap map) {
		if(map.containsKey("size"))
			size = (float) map.getDouble("size");
	}
	
	Map<String, ?> toMap() { 
		return Collections.singletonMap("size", Double.valueOf(size));
	}
}
