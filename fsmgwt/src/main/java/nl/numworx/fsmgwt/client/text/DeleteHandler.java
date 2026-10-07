package nl.numworx.fsmgwt.client.text;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Model;

public class DeleteHandler implements KeyDownHandler {

	private Model model;
	private AbstractViewer viewer;

	public DeleteHandler(AbstractViewer viewer) {
		this.model = viewer.getModel();
		this.viewer = viewer;
	}

	@Override
	public void onKeyDown(KeyDownEvent e) {
		if (e.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE || e.getNativeKeyCode() == KeyCodes.KEY_DELETE)
		{
			model.destroy();
			e.stopPropagation();
			e.preventDefault();
			viewer.paint();
		}
	}

}
