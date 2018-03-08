package nl.numworx.geodefinergwt.client;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import fi.euclides.model.Punt;
import fi.euclides.model.math.Numbers;
import nl.numworx.geodefiner.common.AbstractCirkelLabelHandler;
import nl.numworx.geodefinergwt.client.ui.TekstPopup;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;

public class CirkelRadiusHandler extends AbstractCirkelLabelHandler implements Owner {
	Punt p;
	String text;
	private boolean fuse;

	public CirkelRadiusHandler(String string) {
		super(string);
	}

	@Override
	protected void attachLabel(Punt p) {
		Widget root = getTracker().adapt(Widget.class);
		if(root == null)
			root = RootPanel.get();
		final int x = (int) p.getXd() + root.getAbsoluteLeft(); // OFFSETX 
		final int y = (int) p.getYd() + root.getAbsoluteTop() + 5; // OFFSETY
		this.p = p;
		this.text = ""; // empty
		final TekstPopup tf  = new TekstPopup(this, false);
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
		try {
			double d = NumberFormat.getDecimalFormat().parse(text);
			Numbers dd = Numbers.createDouble(d);
			build(p,dd);
			getTracker().paint();
		} catch (NumberFormatException e) {
			setStatus(e.toString());
		}
	}


}
