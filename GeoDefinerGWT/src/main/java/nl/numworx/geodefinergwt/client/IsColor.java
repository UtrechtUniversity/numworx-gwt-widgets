package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.math.Numbers;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;

public class IsColor extends nl.numworx.geodefiner.common.math.IsColor {

	@Inject IsColor() {
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
	
}
