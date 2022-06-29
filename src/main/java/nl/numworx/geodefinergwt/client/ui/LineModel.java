package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefinergwt.client.IsLineType;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import java.util.Map;

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
	DefaultAdapter.getDefault(item).put(getStroke(width, type));
    super.installLight();
	IsLineType linetype = item.adapt(IsLineType.class);
	if (linetype != null) linetype.updateLineType();
  }
  
  @Override
  public void fromLightMap(ObjectMap value) {
	  super.fromLightMap(value);
	  StrokeStyle s = item.adapt(StrokeStyle.class);
	  if (s == null) s = new StrokeStyle(1.0, null);
	  width = (float) s.lineWidth;

	  if (value.containsKey("type")) 
		  type = LineType.valueOf(value.getString("type"));
	  else {
		  double[] dashes = s.dash;
		  if (dashes == null) type = LineType.SOLID;
		  else if (dashes.length == 4) type = LineType.DASHDOTTED;
		  else if (dashes[0] == s.lineWidth) type = LineType.DOTTED;
		  else type = LineType.DASHED;
	  }
  }
  
  @Override
  public Map<String,Object> toLightMap() {
	  Map<String,Object> m = super.toLightMap();
	  m.put("type", type.name());
	  return m;
  }
}
