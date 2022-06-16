package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;

public class IsColor extends nl.numworx.geodefiner.common.math.IsColor {

	private Label label;
	private IsColor chain;

	@Inject IsColor() {
	}

	private IsColor(Label l, IsColor adapt) {
		this.label = l;
		this.chain = adapt;
	}

	@Override
	protected Integer getColorA(Destroyable a) {
		ColorStyle s = a.adapt(ColorStyle.class);
		if (s == null) s = ColorHandler.colors[6];		
		return s.getRGB();
	}

	@Override
	protected Integer getColorB(Destroyable b) {
		if (b instanceof Label) {
			Label l = (Label) b;
			Numbers value = l.value;
			int n = value.intValue();
			if (n >= 0 && n < ColorHandler.colors.length) {
				return ColorHandler.colors[n].getRGB();
			}
		}
		return getColorA(b);
	}
	
	@Override
	public boolean define(Label l) {
		DefaultAdapter adapter = DefaultAdapter.getDefault(l.getDepend()[0]);
		adapter.put(new IsColor(l, adapter.adapt(IsColor.class)));
		return super.define(l);
	}

	public void updateColor() {
		test(label);
		if (chain != null) chain.updateColor();
	}

}
