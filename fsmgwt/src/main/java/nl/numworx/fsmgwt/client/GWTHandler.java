package nl.numworx.fsmgwt.client;

import java.util.Objects;
import java.util.Vector;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import fi.euclides.event.TrackerContext;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.math.Numbers;
import nl.numworx.fsm.shared.UnifiedHandler;
import nl.numworx.fsmgwt.client.text.TekstPopup;

public class GWTHandler extends UnifiedHandler {

	private final class PopupTimer extends Timer implements HasText {
		final Destroyable item;
		int mx, my;
		public PopupTimer(Numbers x, Numbers y, Destroyable d) {
			item = d;
			mx = x.intValue();
			my = y.intValue();
		}

		@Override
		public void run() {	
			timerDone = true;
			useTimer = null;
			RootPanel root = RootPanel.get();
			TekstPopup tf = new TekstPopup(this, true);
			AbstractViewer viewer = getTracker().adapt(AbstractViewer.class);
			final int offx=viewer.clipLeft().intValue();
			final int offy=viewer.clipTop().intValue();
			final int x = mx + root.getAbsoluteLeft()    - offx; // OFFSETX 
			final int y = my + root.getAbsoluteTop() + 5 - offy; // OFFSETY
			tf.setAutoHideEnabled(true);
			tf.setAutoHideOnHistoryEventsEnabled(true);
			tf.setPopupPositionAndShow(new PositionCallback() {
				
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					tf.setPopupPosition(x, y);
				}
			});
		
		}

		@Override
		public String getText() {
			return Objects.toString(getTracker().getMapper().toString(item), "");
		}

		@Override
		public void setText(String text) {
			getTracker().getMapper().rename(item, text);
			getTracker().paint();
		}
	}


	public GWTHandler() {
		super("FSM");
		
	}

	Timer useTimer;
	boolean timerDone;
	private void startTimer(Numbers x, Numbers y, Destroyable d) {
		cancelTimer();
		
		useTimer = new PopupTimer(x,y, d);
		useTimer.schedule(1000);
	}
	public void cancelTimer() {
		if (useTimer != null) {
			useTimer.cancel();
			useTimer = null;
			timerDone = false;
		}
	}
	

	@Override
	protected void startPointerClicked(Numbers x, Numbers y, TrackerContext context) {
		Vector<Destroyable> selection = context.selection();
		if (selection.size() == 1) {
			Destroyable object = selection.firstElement();
			startTimer(x, y, object);		
		}
	}
	@Override
	public void pointerPressed(Numbers x, Numbers y, TrackerContext context) {
		cancelTimer();
		super.pointerPressed(x, y, context);
	}

}
