package nl.numworx.geodefinergwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import fi.euclides.event.EventHandler;
import fi.euclides.formuleobjects.FormuleParser;
import fi.euclides.formuleobjects.ParseException;
import fi.euclides.model.Label;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Volgpunt;
import nl.numworx.geodefinergwt.client.ui.TekstPopup;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;
import nl.tue.win.riaca.openmath.lang.OMObject;

public class FormuleHandler extends EventHandler implements Owner {

	public FormuleHandler(String string, Definitions d) {
		super(string);
		definitions = d;
	}
	String text;
	private boolean fuse;

	private Definitions definitions;

	/* (non-Javadoc)
	 * @see fi.euclides.event.EventHandler#command()
	 */
	@Override
	public void command() {
		setStatus(string);
		Widget root = getTracker().adapt(Widget.class);
		text = "";
		fuse = false;
		final int x = root.getAbsoluteLeft() + root.getOffsetWidth()/2; // OFFSETX 
		final int y = root.getAbsoluteTop() + root.getOffsetHeight()/2;
		final TekstPopup tf  = new TekstPopup(this, false);
		tf.setAutoHideEnabled(true);
		tf.setAutoHideOnHistoryEventsEnabled(true);
		fuse = false;
		tf.setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				tf.setPopupPosition(x-offsetWidth/2, y-offsetHeight/2);
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
//		Label label = new Label();
//		label.setString(text);
//		Widget root = getTracker().adapt(Widget.class);
//		final int x = root.getAbsoluteLeft() + root.getOffsetWidth()/2;
//		final int y = root.getAbsoluteTop() + root.getOffsetHeight()/2;
//		label.setX(x);
//		label.setY(y);
//		DefaultAdapter.getDefault(label).put(Boolean.TRUE);
//		getModel().add(label);
		OMObject object;
		try {
			object = new FormuleParser(text).parse();
			definitions.define("$f" + text + "@", object);
		} catch (Exception e) {
			setStatus(e.toString());
			return;
		}
	}

}
