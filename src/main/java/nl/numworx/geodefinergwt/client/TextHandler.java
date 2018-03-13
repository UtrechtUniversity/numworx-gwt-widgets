package nl.numworx.geodefinergwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.euclides.event.EventHandler;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.math.Numbers;
import nl.numworx.geodefiner.common.AbstractTextHandler;
import nl.numworx.geodefiner.common.Volgpunt;
import nl.numworx.geodefinergwt.client.ui.TekstPopup;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;

public class TextHandler extends AbstractTextHandler implements Owner {

	public TextHandler(String string) {
		super(string);
	}

	Punt p;
	String text;
	private boolean fuse;
	@Override
	protected void attachLabel(Punt p) {
		Widget root = getTracker().adapt(Widget.class);
		if(root == null)
			root = RootPanel.get();
		final int x = (int) p.getXd() + root.getAbsoluteLeft(); // OFFSETX 
		final int y = (int) p.getYd() + root.getAbsoluteTop() + 5; // OFFSETY
		this.p = p;
		this.text = ""; // empty
		final TekstPopup tf  = new TekstPopup(this, true);
		tf.setAutoHideEnabled(true);
		tf.setAutoHideOnHistoryEventsEnabled(true);
		fuse = false;
		tf.setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				tf.setPopupPosition(x, y);
			}
		});
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		if(fuse||text.isEmpty()) return;
		fuse = true;
		this.text = text;
		Label label = new Label();
		label.setString(text);
		Volgpunt v = new Volgpunt(p);
		v.setDxy(Numbers.createInteger(6),Numbers.createInteger(-5));
		v.setFree(false);
		label.setP(v);
		getModel().add(label);
		getTracker().paint();
	}


}
