package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;

public class IsLineType extends nl.numworx.geodefiner.common.math.IsLineType {

	@Inject IsLineType() {
	}

	private IsLineType(Label l, IsLineType adapt) {
		label = l;
		chain = adapt;
	}

	private Label label;
	private IsLineType chain;
	
	@Override
	protected LineType getLineTypeA(Destroyable a) {
		StrokeStyle ss = a.adapt(StrokeStyle.class);
		if (ss == null) return LineType.SOLID;
		double dash[] = ss.dash, width = ss.lineWidth;
		if (dash == null) return LineType.SOLID;
		if (dash.length == 4) return LineType.DASHDOTTED;
		return dash[0] == width ? LineType.DOTTED : LineType.DASHED;
	}

	void updateLineType() {
		test(label);
		if (chain != null) chain.updateLineType();
	}
	
	@Override
	public boolean define(Label l) {
		DefaultAdapter adapter = DefaultAdapter.getDefault(l.getDepend()[0]);
		adapter.put(new IsLineType(l, adapter.adapt(IsLineType.class)));
		adapter = DefaultAdapter.getDefault(l.getDepend()[1]);
		adapter.put(new IsLineType(l, adapter.adapt(IsLineType.class)));
		return super.define(l);
	}

}
