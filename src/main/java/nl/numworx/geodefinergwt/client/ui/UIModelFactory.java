package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.UIModel;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;

public class UIModelFactory extends nl.numworx.geodefiner.common.UIModelFactory  {

	@Override
	public UIModel<?, ?> build(Destroyable d) {
		if(d == null) throw new NullPointerException();
		if( d instanceof Label)
			return new TextModel().init(d);
		return new ColorModel<Destroyable>().init(d);
	}

}
