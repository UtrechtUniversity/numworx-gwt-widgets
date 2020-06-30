package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.LineType;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.util.DefaultAdapter;

public class LineModel extends ColorModel<Destroyable> {

	float width = 1.0f;
	LineType type = LineType.SOLID;
//	static final String dashes[] = { 
//			null, 
//			"1, 3" ,
//			"5, 3" , 
//			"5, 3, 1, 3"
//	};
	static final double dash[][] = {
			null,
			{ 1, 3 },
			{ 5, 3 },
			{ 5, 3, 1, 3 }
	};
	boolean rigid; // default beweeglijk

	@Override
	public void install(Destroyable item) {
	  super.install(item);
	  DefaultAdapter adapter = DefaultAdapter.getDefault(item);
	  adapter.put(getStroke(width, type));
      adapter.put(Boolean.valueOf(rigid));
	}


  static public StrokeStyle getStroke(float f, LineType lineType) {
    return new StrokeStyle(f, dash[lineType.ordinal()]);
  }

  static public StrokeStyle getStroke(LineType lineType) {
    return new StrokeStyle(1f, dash[lineType.ordinal()]);
  }

	/* (non-Javadoc)
	 * @see nl.numworx.geodefinergwt.client.ui.ColorModel#fromMap(nl.uu.fi.dwo.interaction.client.json.ObjectMap)
	 */
	@Override
	public void fromMap(ObjectMap value) {
		super.fromMap(value);
        rigid = value.getBoolean("rigid", false);
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
	@Inject public LineModel() { }


  @Override
  public void installLight() {
    super.installLight();
    DefaultAdapter.getDefault(item).put(getStroke(width, type));
  }
}
