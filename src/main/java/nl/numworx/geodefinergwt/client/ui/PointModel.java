package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import nl.numworx.geodefiner.common.PointType;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Groep;
import fi.euclides.model.Punt;
import fi.euclides.model.algo.FreePoint;
import fi.euclides.util.DefaultAdapter;

public class PointModel extends ColorModel<Punt> {
	private static final Logger LOG = Logger.getLogger(PointModel.class.getName());
	int   size = 5;
	PointType  type = PointType.DISK;
	public boolean rigid = true;

	public void install() {
		if(item instanceof FreePoint) {
			FreePoint r = (FreePoint) item;
			r.setFree(!rigid);
		} else if (item instanceof Groep) {
          Groep g = (Groep) item;
          if (g.prototype() instanceof FreePoint)
            g.setFree(!rigid);
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

	@Override
	public Map<String, Object> toMap() {
		LOG.info("item = " + item);
		return super.toMap();
	}

	@Inject public PointModel() { }
	
}
