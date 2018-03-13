package nl.numworx.geodefinergwt.client.toolbox;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.user.client.ui.ToggleButton;

import fi.euclides.event.EventHandler;

@Singleton
public class RadioMode {

	@Inject public RadioMode() {
	}

	private ToggleButton downBtn;
	public final Map<EventHandler, ToggleButton> toggles = new HashMap<>();

	public void down(ToggleButton btn) {
		if(downBtn != null && downBtn != btn)
			downBtn.setDown(false);
		downBtn = btn;
		if(btn != null)
			btn.setDown(true);
		
	}
}
