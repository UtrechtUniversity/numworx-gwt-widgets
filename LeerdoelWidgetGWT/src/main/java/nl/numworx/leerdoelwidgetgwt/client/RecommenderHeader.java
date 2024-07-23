package nl.numworx.leerdoelwidgetgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.numworx.leerdoelwidgetgwt.client.locale.LeerdoelWidgetMessages;

public class RecommenderHeader extends Composite implements HasValueChangeHandlers<Boolean>, ClickHandler {
	private static final LeerdoelWidgetMessages rb = GWT.create(LeerdoelWidgetMessages.class);
	private static final int HEIGHT = 42;
	private IsWidget center;
	private boolean down = true; // nog even...
	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
		updateUpDown();
	}

	public RecommenderHeader() {
		Label header = new Label(rb.header());
		header.setStylePrimaryName("recommender-header");
		initWidget(header);
		header.addClickHandler(this);
	}

	public IsWidget getCenter() {
		return center;
	}

	public void setCenter(IsWidget center) {
		this.center = center;
		Scheduler.get().scheduleDeferred(this::updateUpDown);
	}

	private void updateUpDown() {
		RootLayoutPanel root = RootLayoutPanel.get();
		Widget w = Widget.asWidgetOrNull(center);
		if (w != null && w.isAttached()) root.setWidgetVisible(w, down);
		setStyleDependentName("down", down);
		ValueChangeEvent.fire(this, down);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void onClick(ClickEvent event) {
		setDown(!down);		
	}
	
	public int getHeight() { 
		return HEIGHT;
	}
	
}
