package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import fi.euclides.event.EventHandler;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;

public class AddHoekPuntHandler extends EventHandler implements Owner {

	@Inject AddHoekPuntHandler() {
		super(GeoDefinerGWT.MESSAGES.AddBissectriceHandler_0());
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return "0";
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

}
