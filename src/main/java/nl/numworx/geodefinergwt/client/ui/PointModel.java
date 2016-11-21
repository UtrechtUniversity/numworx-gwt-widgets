package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.PointType;
import fi.euclides.model.Punt;
import fi.euclides.model.algo.FreePoint;

public class PointModel extends ColorModel<Punt> {
	int   size = 5;
	PointType  type = PointType.DISK;
	public boolean rigid = true;

	public void install() {
		if(item instanceof FreePoint) {
			FreePoint r = (FreePoint) item;
			r.setFree(!rigid);
		}
		super.install();
	}

}
