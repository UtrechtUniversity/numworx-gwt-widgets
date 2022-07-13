package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;

public class IsLineType extends nl.numworx.geodefiner.common.math.IsLineType {

	@Inject IsLineType() {
	}
	
	@Override
	protected LineType getLineTypeA(Destroyable a) {
		StrokeStyle ss = a.adapt(StrokeStyle.class);
		if (ss == null) return LineType.SOLID;
		double dash[] = ss.dash, width = ss.lineWidth;
		if (dash == null) return LineType.SOLID;
		if (dash.length == 4) return LineType.DASHDOTTED;
		return dash[0] == width ? LineType.DOTTED : LineType.DASHED;
	}
	
}
