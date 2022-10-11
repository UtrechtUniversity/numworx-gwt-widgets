package nl.numworx.geodefinergwt.client;

import java.util.logging.Logger;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;

public class IsLineType extends nl.numworx.geodefiner.common.math.IsLineType {
	
	Logger LOG = Logger.getLogger("isLineType");

	@Inject IsLineType() {
	}
	
	@Override
	protected LineType getLineTypeA(Destroyable a) {
		StrokeStyle ss = a.adapt(StrokeStyle.class);
		if (ss == null) return x(LineType.SOLID);
		double dash[] = ss.dash, width = ss.lineWidth;
		if (dash == null) return x(LineType.SOLID);
		if (dash.length == 4) return x(LineType.DASHDOTTED);
		return x(dash[0] == width ? LineType.DOTTED : LineType.DASHED);
	}

	private LineType x(LineType object) {
		LOG.severe(object.name());
		return object;
	}

	@Override
	protected boolean test(Label l) {
		LOG.severe("linetype " + l);
		boolean test = super.test(l);
		LOG.severe("TEST = " + test);
		return test;
	}
	
}
