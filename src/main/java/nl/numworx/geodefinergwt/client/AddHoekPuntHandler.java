package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import fi.euclides.event.TrackerContext;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.math.Numbers;
import nl.numworx.geodefiner.common.AbstractAddHoekPuntHandler;
import nl.numworx.geodefinergwt.client.ui.TekstPopup;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;

public class AddHoekPuntHandler extends AbstractAddHoekPuntHandler implements Owner {
	private TekstPopup tf;
	private boolean fuse;

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
		if(fuse||text.isEmpty()) return;
		fuse = true;
		try {
			double d = NumberFormat.getDecimalFormat().parse(text);
			Numbers dd = Numbers.createDouble(d);
			build(p1, p2, dd);
			getModel().clearSelection();
			getTracker().paint();
		} catch (NumberFormatException e) {
			setStatus(e.toString());
		}
	}

	@Override
	protected void createTrack() {
		Widget root = getTracker().adapt(Widget.class);
		if(root == null)
			root = RootPanel.get();
		AbstractViewer viewer = getTracker().adapt(AbstractViewer.class);
		final int offx=viewer.clipLeft().intValue();
		final int offy=viewer.clipTop().intValue();
		final int x = p2.getX().intValue() + root.getAbsoluteLeft()    - offx; // OFFSETX 
		final int y = p2.getY().intValue() + root.getAbsoluteTop() + 5 - offy; // OFFSETY
		tf = new TekstPopup(this, true);
		tf.setAutoHideEnabled(true);
		tf.setAutoHideOnHistoryEventsEnabled(true);
		setStatus(GeoDefinerGWT.MESSAGES.AddHoekPuntHandler_0());
		fuse = false;
		tf.setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				tf.setPopupPosition(x, y);
			}
		});
		
	}

	@Override
	public void pointerPressed(Numbers x, Numbers y, TrackerContext context) {
		if (tf != null) {
				tf.hide(true);
				tf = null;
		}
		super.pointerPressed(x, y, context);
	}

}
