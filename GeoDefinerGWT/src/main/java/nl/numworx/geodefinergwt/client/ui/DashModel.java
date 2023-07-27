package nl.numworx.geodefinergwt.client.ui;

import javax.inject.Inject;

import fi.euclides.model.Destroyable;
import nl.numworx.geodefiner.common.AbstractDashModel;
import nl.numworx.geodefiner.common.UIModel;

public class DashModel extends AbstractDashModel<Void> implements UIModel<Destroyable, Void> {

	@Inject DashModel() {
	}

	@Override
	public Void editor() {
		return null;
	}

}
