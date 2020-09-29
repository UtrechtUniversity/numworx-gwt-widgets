package nl.numworx.geodefinergwt.client;

import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.euclides.event.EventHandler;
import fi.euclides.event.TrackerContext;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.LabelDelegate;
import nl.numworx.geodefiner.common.AbstractTextHandler;
import nl.numworx.geodefiner.common.Volgpunt;
import nl.numworx.geodefiner.common.math.ToC;
import nl.numworx.geodefinergwt.client.ui.TekstPopup;
import nl.numworx.geodefinergwt.client.ui.TekstPopup.Owner;

public class TextHandler extends AbstractTextHandler implements Owner {

	public TextHandler(String string) {
		super(string);
	}

	Punt p;
	String text;
	private boolean fuse;
	private TekstPopup tf;
	@Override
	protected void attachLabel(Punt p) {
		Widget root = getTracker().adapt(Widget.class);
		if(root == null)
			root = RootPanel.get();
		AbstractViewer viewer = getTracker().adapt(AbstractViewer.class);
		final int offx=viewer.clipLeft().intValue();
		final int offy=viewer.clipTop().intValue();
		final int x = p.getX().intValue() + root.getAbsoluteLeft()    - offx; // OFFSETX 
		final int y = p.getY().intValue() + root.getAbsoluteTop() + 5 - offy; // OFFSETY
		this.p = p;
		this.text = ""; // empty
		tf = new TekstPopup(this, true);
		tf.setAutoHideEnabled(true);
		tf.setAutoHideOnHistoryEventsEnabled(true);
		fuse = false;
		setStatus(string);
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
		LabelDelegate ld = getTracker().getRegistered(ToC.TYPE);
		Label label;
// find label
		Vector<Destroyable> lijnen = getModel().getLijnen();
		for(Destroyable item: lijnen) {
			if (item instanceof Label) {
				label = (Label) item;
				if (label.getRegistered() == ld && label.getDepend()[0] == p) {
// if match replace text
					label.setString(text);
					label.notifyObservers();
					getTracker().paint();
					return;
				}
			}
		}
		
		
		this.text = text;
		Destroyable[] depend = ld.createDepend(1);
		depend[0] = p;
		label = ld.define(depend);
		label.setString(text);
		Volgpunt v = new Volgpunt(p);
		v.setDxy(Numbers.createInteger(6),Numbers.createInteger(-5));
		v.setFree(false);
		label.setP(v);
		getModel().add(label);
		getTracker().paint();
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
