package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.PointType;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Punt;
import fi.euclides.model.algo.FreePoint;
import fi.euclides.util.DefaultAdapter;

public class PointModel extends ColorModel<Punt> {
	int   size = 5;
	PointType  type = PointType.DISK;
	public boolean rigid = true;

	public void install() {
		if(item instanceof FreePoint) {
			FreePoint r = (FreePoint) item;
			r.setFree(!rigid);
		}
		DefaultAdapter adapter = DefaultAdapter.getDefault(item);
		adapter.put(Float.valueOf(size));
		super.install();
	}

	/* (non-Javadoc)
	 * @see nl.numworx.geodefinergwt.client.ui.ColorModel#fromMap(nl.uu.fi.dwo.interaction.client.json.ObjectMap)
	 */
	@Override
	public void fromMap(ObjectMap value) {
		super.fromMap(value);
		size = value.getInt("size");
		type = PointType.valueOf(value.getString("type"));
		rigid = value.getBoolean("rigid", true);
	}

//	/* (non-Javadoc)
//	 * @see nl.numworx.geodefinergwt.client.ui.ColorModel#toMap()
//	 */
//	@Override
//	public Map<String, Object> toMap() {
//		// TODO Auto-generated method stub
//		return super.toMap();
//	}
//
	
}
