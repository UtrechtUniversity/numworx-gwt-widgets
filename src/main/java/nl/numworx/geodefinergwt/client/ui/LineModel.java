package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.LineType;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Destroyable;
import fi.euclides.util.DefaultAdapter;

public class LineModel extends ColorModel<Destroyable> {

	float width = 1.0f;
	LineType type = LineType.SOLID;
	String dashes[] = { 
			null, 
			"1, 3" ,
			"5, 3" , 
			"5, 3, 1, 3"
	};
	
	
	/* (non-Javadoc)
	 * @see nl.numworx.geodefinergwt.client.ui.ColorModel#install()
	 */
	@Override
	public void install() {
		super.install();
		DefaultAdapter.getDefault(item).put(new StrokeStyle(Float.toString(width), dashes[type.ordinal()]));
	}

	/* (non-Javadoc)
	 * @see nl.numworx.geodefinergwt.client.ui.ColorModel#fromMap(nl.uu.fi.dwo.interaction.client.json.ObjectMap)
	 */
	@Override
	public void fromMap(ObjectMap value) {
		super.fromMap(value);
		try {
			width = (float) value.getDouble("width");
			if(Float.isNaN(width)) width = 1.0f;
		} catch (Exception e) {
			width = 1.0f;
		}
		try {
			type = LineType.valueOf(value.getString("type"));
		} catch (Exception e) {
			type = LineType.SOLID;
		}
	}

}
